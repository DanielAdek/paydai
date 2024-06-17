package com.paydai.api.application;

import com.paydai.api.domain.exception.ApiRequestException;
import com.paydai.api.domain.exception.ConflictException;
import com.paydai.api.domain.exception.InternalServerException;
import com.paydai.api.domain.model.*;
import com.paydai.api.domain.repository.EmailRepository;
import com.paydai.api.domain.repository.StripeAccountRepository;
import com.paydai.api.domain.service.AccountService;
import com.paydai.api.infrastructure.config.AppConfig;
import com.paydai.api.presentation.dto.account.StripeAccountDtoMapper;
import com.paydai.api.presentation.dto.account.StripeAccountRecord;
import com.paydai.api.presentation.enums.AccountType;
import com.paydai.api.presentation.request.AccountLinkRequest;
import com.paydai.api.presentation.request.AccountRequest;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
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

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
  private final StripeAccountRepository repository;
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

      StripeAccountModel stripeAccountExist = repository.findByUser(userModel.getUserId());

      if (stripeAccountExist != null) throw new ConflictException("Stripe account already exit!");

      EmailModel emailModel = emailRepository.findPersonalEmailByUser(userModel.getUserId());

      AccountCreateParams.Type accountType = emailModel.getUser().getUserType().equals(UserType.MERCHANT) ? AccountCreateParams.Type.STANDARD :
        AccountCreateParams.Type.EXPRESS;

      AccountCreateParams accountCreateParams = AccountCreateParams.builder().setEmail(emailModel.getEmail()).setType(accountType).build();

      Account account = Account.create(accountCreateParams);

      if (account == null) throw new ApiRequestException("Account did not create");

      StripeAccountModel buildStripeAccount = StripeAccountModel.builder().stripeId(account.getId()).userId(userModel.getUserId()).personalEmail(emailModel.getEmail()).build();

      StripeAccountModel stripeAccountModel = repository.save(buildStripeAccount);

      stripeAccountModel.setStripeId(account.getId());

      StripeAccountRecord stripeAccountRecord = stripeAccountDtoMapper.apply(stripeAccountModel);

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
