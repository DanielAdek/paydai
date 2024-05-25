package com.paydai.api.application.account;

import com.paydai.api.domain.exception.InternalServerException;
import com.paydai.api.domain.service.AccountService;
import com.paydai.api.infrastructure.config.AppConfig;
import com.paydai.api.presentation.dto.StripeAccountDto;
import com.paydai.api.presentation.response.JapiResponse;
import com.paydai.api.presentation.response.StriptAccountResponse;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
 private final AppConfig config;
  private final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

  /**
   * @desc This method is used to create stripe account
   * @return it returns a json data with stripe accountId
   */
  public JapiResponse createAccount() {
    try {
      Account account = Account.create(AccountCreateParams.builder().build());
      System.out.println(account);
      return JapiResponse.success(account.getId());
    } catch (Exception e) {
      logger.error("Stripe::Error:: " + e.getMessage());
      throw new InternalServerException(e.getMessage());
    }
  }

  /**
   * @desc This method links the created account to stripe
   * @param stripeAccountDto
   * @return it returns a json response containing account Url
   */
  public JapiResponse createAccountLink(StripeAccountDto stripeAccountDto) {
    try {
      String connectedAccountId = stripeAccountDto.getAccountId();

      AccountLink accountLink = AccountLink.create(
        AccountLinkCreateParams.builder()
          .setAccount(connectedAccountId)
          .setReturnUrl(config.getApiBaseUrl() + "/return/" + connectedAccountId)
          .setRefreshUrl(config.getApiBaseUrl() + "/refresh/" + connectedAccountId)
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
