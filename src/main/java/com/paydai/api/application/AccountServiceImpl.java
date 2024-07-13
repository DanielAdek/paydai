package com.paydai.api.application;

import com.paydai.api.domain.exception.ApiRequestException;
import com.paydai.api.domain.exception.ConflictException;
import com.paydai.api.domain.exception.InternalServerException;
import com.paydai.api.domain.model.*;
import com.paydai.api.domain.repository.EmailRepository;
import com.paydai.api.domain.repository.UserRepository;
import com.paydai.api.domain.service.AccountService;
import com.paydai.api.infrastructure.config.AppConfig;
import com.paydai.api.presentation.dto.account.StripeAccountDtoMapper;
import com.paydai.api.presentation.dto.account.StripeAccountRecord;
import com.paydai.api.presentation.request.AccountLinkRequest;
import com.paydai.api.presentation.request.AccountRequest;
import com.paydai.api.presentation.request.OauthRequest;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.oauth.TokenResponse;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
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

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
  private final UserRepository repository;
  private final EmailRepository emailRepository;
  private final StripeAccountDtoMapper stripeAccountDtoMapper;
  private final AppConfig config;

  private final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

  /**
   * @desc This method is used to create stripe account
   * @return it returns a json data with stripe accountId
   */
  public JapiResponse createAccount(AccountRequest payload) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      UserModel userModel = (UserModel) authentication.getPrincipal();

      if (userModel.getStripeId() != null) throw new ConflictException("Stripe account already exit!");

      EmailModel emailModel = emailRepository.findPersonalEmailByUser(userModel.getId());

      AccountCreateParams.Type accountType = emailModel.getUser().getUserType().equals(UserType.MERCHANT) ? AccountCreateParams.Type.STANDARD :
        AccountCreateParams.Type.EXPRESS;

      AccountCreateParams accountCreateParams;

      if (emailModel.getUser().getUserType().equals(UserType.MERCHANT)) {
        accountCreateParams = AccountCreateParams.builder().setEmail(emailModel.getEmail()).setType(accountType).build();
      } else {
        accountCreateParams = AccountCreateParams.builder()
          .setEmail(emailModel.getEmail())
          .setType(AccountCreateParams.Type.EXPRESS)
          .setCapabilities(
            AccountCreateParams.Capabilities.builder()
              .setCardPayments(
                AccountCreateParams.Capabilities.CardPayments.builder()
                  .setRequested(true)
                  .build()
              )
              .setTransfers(
                AccountCreateParams.Capabilities.Transfers.builder().setRequested(true).build()
              )
              .build()
          )
          .setBusinessType(AccountCreateParams.BusinessType.INDIVIDUAL)
          .build();
      }

      Account account = Account.create(accountCreateParams);

      if (account == null) throw new ApiRequestException("Account did not create. Try again");

      userModel.setStripeEmail(account.getId());

      StripeAccountRecord stripeAccountRecord = stripeAccountDtoMapper.apply(userModel);

      return JapiResponse.success(stripeAccountRecord);
    } catch (ConflictException e) {throw e; } catch (Exception e) {
      logger.error("Stripe::Error:: " + e.getMessage());
      throw new InternalServerException(e.getMessage());
    }
  }

  /**
   * @desc This method links the created account to stripe
   * @param payload
   * @return it returns a json response containing account Url
   */
  public JapiResponse createAccountLink(AccountLinkRequest payload) {
    try {
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
    } catch (Exception e) {
      logger.error("Stripe::Error::" + e.getMessage());
      throw new InternalServerException(e.getMessage());
    }
  }

  /**
   * @desc This method authenticate merchants
   * @param payload the oauth payload
   * @return it returns a json response containing account Url
   */
  @Override
  public JapiResponse authenticate(OauthRequest payload) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      UserModel userModel = (UserModel) authentication.getPrincipal();

      Map<String, Object> hashMap = new HashMap<>();

      hashMap.put("grant_type", "authorization_code");

      hashMap.put("code", payload.getCode());

      TokenResponse response = OAuth.token(hashMap, null);

      hashMap.put("stripeId", response.getStripeUserId());

      UserModel stripeUser = repository.findUserById(userModel.getId());

      if (stripeUser == null) {
        stripeUser.setStripeId(Objects.requireNonNull(response.getStripeUserId()));
      }

      return JapiResponse.success(hashMap);
    } catch (Exception ex) {
      logger.info("An error occurred: {} ", ex.getMessage());
      throw new InternalServerException(ex.getMessage(), ex);
    }
  }

  /**
   * @desc This method authenticate merchants
   * @param accountId the account stripe id
   * @return it returns a json response containing account Url
   */
  @Override
  public JapiResponse getStripeAccount(String accountId) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      UserModel userModel = (UserModel) authentication.getPrincipal();

      Account account = Account.retrieve(userModel.getStripeId());

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
    } catch (Exception ex) {
      logger.info("An error occurred: {} ", ex.getMessage());
      throw new InternalServerException(ex.getMessage(), ex);
    }
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
