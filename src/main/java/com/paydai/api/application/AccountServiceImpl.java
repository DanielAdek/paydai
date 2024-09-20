package com.paydai.api.application;

import com.paydai.api.domain.annotation.TryCatchException;
import com.paydai.api.domain.exception.ApiRequestException;
import com.paydai.api.domain.exception.ConflictException;
import com.paydai.api.domain.exception.InternalServerException;
import com.paydai.api.domain.model.*;
import com.paydai.api.domain.repository.AccountLedgerRepository;
import com.paydai.api.domain.repository.EmailRepository;
import com.paydai.api.domain.repository.UserRepository;
import com.paydai.api.domain.service.AccountService;
import com.paydai.api.infrastructure.config.AppConfig;
import com.paydai.api.presentation.request.AccountLinkRequest;
import com.paydai.api.presentation.request.AccountRequest;
import com.paydai.api.presentation.request.OauthRequest;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.LoginLink;
import com.stripe.model.oauth.TokenResponse;
import com.stripe.net.RequestOptions;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.LoginLinkCreateOnAccountParams;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.stripe.net.OAuth;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
  private final AppConfig config;
  private final UserRepository repository;
  private final EmailRepository emailRepository;
  private final AccountLedgerRepository accountLedgerRepository;

  private final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

  /**
   * @desc This method is used to create stripe account
   * @return it returns a json data with stripe accountId
   */
  @TryCatchException
  @Transactional
  public JapiResponse createAccount(AccountRequest payload) throws StripeException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserModel userModel = (UserModel) authentication.getPrincipal();

    if (userModel.getStripeId() != null) throw new ConflictException("Stripe account already exists!");

    EmailModel emailModel = emailRepository.findPersonalEmailByUser(userModel.getId());

    AccountCreateParams.Type accountType = emailModel.getUser().getUserType().equals(UserType.MERCHANT)
      ? AccountCreateParams.Type.STANDARD
      : AccountCreateParams.Type.EXPRESS;

    AccountCreateParams.Builder accountCreateParams = AccountCreateParams.builder()
      .setEmail(emailModel.getEmail())
      .setType(accountType);

    String tosAcceptance = userModel.getCountryCode().equals("US") ? "full" : "recipient";

    // Only request capabilities if tosAcceptance is "full"
    if (emailModel.getUser().getUserType().equals(UserType.MERCHANT)) {
      accountCreateParams.setDefaultCurrency("usd");
    } else {
      if (tosAcceptance.equals("full")) {
        // Request capabilities only if service agreement is "full"
        accountCreateParams.setCapabilities(
          AccountCreateParams.Capabilities.builder()
            .setCardPayments(
              AccountCreateParams.Capabilities.CardPayments.builder()
                .setRequested(true)
                .build()
            )
            .setTransfers(
              AccountCreateParams.Capabilities.Transfers.builder()
                .setRequested(true)
                .build()
            ).build()
        );
      } else {
        // For "recipient", only request transfers
        accountCreateParams.setCapabilities(
          AccountCreateParams.Capabilities.builder()
            .setTransfers(
              AccountCreateParams.Capabilities.Transfers.builder()
                .setRequested(true)
                .build()
            ).build()
        );
      }
    }

    accountCreateParams
      .setTosAcceptance(
        AccountCreateParams.TosAcceptance.builder()
          .setServiceAgreement(tosAcceptance)
          .build()
      )
      .setCountry(userModel.getCountryCode());

    Account account = Account.create(accountCreateParams.build());

    if (account == null) throw new ApiRequestException("Account did not create. Try again");

    AccountLedgerModel accountLedgerModel = accountLedgerRepository.findAccountLedgerByUser(userModel.getId());

    if (accountLedgerModel == null) {
      accountLedgerRepository.save(
        AccountLedgerModel.builder()
          .balance(0.0)
          .pendBal(0.0)
          .currency(account.getDefaultCurrency())
          .user(userModel)
          .build()
      );
    }

    // Update column stripe for user
    repository.updateUserStripe(userModel.getId(), account.getId(), emailModel.getEmail());

    Map<String, Object> response = new HashMap<>();
    response.put("userId", userModel.getId());
    response.put("stripeId", account.getId());

    return JapiResponse.success(response);
  }

  /**
   * @desc This method links the created account to stripe
   * @param payload
   * @return it returns a json response containing account Url
   */
  @TryCatchException
  public JapiResponse createAccountLink(AccountLinkRequest payload) throws StripeException {
    String connectedAccountId = payload.getAccountId();

    AccountLink accountLink = AccountLink.create(
      AccountLinkCreateParams.builder()
        .setAccount(connectedAccountId)
        .setReturnUrl(config.getPaydaiClientBaseUrl() + "/stripereturn/")
        .setRefreshUrl(config.getPaydaiClientBaseUrl() + "/striperefresh/" + connectedAccountId)
        .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
        .build()
    );
    return JapiResponse.success(accountLink.getUrl());
  }

  @Override
  @TryCatchException
  public JapiResponse getStripeLoginLink() throws StripeException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserModel userModel = (UserModel) authentication.getPrincipal();

    LoginLinkCreateOnAccountParams params = LoginLinkCreateOnAccountParams.builder().build();

    LoginLink loginLink = LoginLink.createOnAccount(userModel.getStripeId(), params);

    Map<String, Object> response = new HashMap<>();
    response.put("url", loginLink.getUrl());
    response.put("createdAt", loginLink.getCreated());

    return JapiResponse.success(response);
  }

  /**
   * @desc This method authenticate merchants
   * @param payload the oauth payload
   * @return it returns a json response containing account Url
   */
  @Override
  @TryCatchException
  public JapiResponse authenticate(OauthRequest payload) throws StripeException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserModel userModel = (UserModel) authentication.getPrincipal();

    Map<String, Object> hashMap = new HashMap<>();

    hashMap.put("grant_type", "authorization_code");

    hashMap.put("code", payload.getCode());

    TokenResponse response = OAuth.token(hashMap, null);

    hashMap.put("stripeId", response.getStripeUserId());

    // Update column stripe for user
    repository.updateUserStripe(userModel.getId(), response.getStripeUserId(), "");

    return JapiResponse.success(hashMap);
  }

  /**
   * @desc This method authenticate merchants
   * @param accountId the account stripe id
   * @return it returns a json response containing account Url
   */
  @Override
  @TryCatchException
  public JapiResponse getStripeAccount(String accountId) throws StripeException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserModel userModel = (UserModel) authentication.getPrincipal();

    Account account = Account.retrieve(userModel.getStripeId());

    AccountLedgerModel accountLedgerModel = accountLedgerRepository.findAccountLedgerByUser(userModel.getId());

    if (accountLedgerModel == null) {
      accountLedgerRepository.save(
        AccountLedgerModel.builder()
          .balance(0.0)
          .pendBal(0.0)
          .currency(account.getDefaultCurrency())
          .user(userModel)
          .build()
      );
    }

    Map<String, Object> hashMap = new HashMap<>();

    if (account != null) {
      hashMap.put("type", account.getType());
      hashMap.put("email", account.getEmail());
      hashMap.put("stripeId", account.getId());
      hashMap.put("country", account.getCountry());
      hashMap.put("detailsSubmitted", account.getDetailsSubmitted());
      hashMap.put("defaultCurrency", account.getDefaultCurrency());
    }

    return JapiResponse.success(hashMap);
  }

  /**
   * @return
   */
  public String serveIndex() {
    try {
      Path path = Paths.get("dist/index.html");
      return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    } catch (Exception e) {
      logger.error("Stripe::Error::" + e.getMessage());
      throw new InternalServerException(e.getMessage());
    }
  }
}
