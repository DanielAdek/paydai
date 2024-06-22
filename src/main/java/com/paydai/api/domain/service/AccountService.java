package com.paydai.api.domain.service;

import com.paydai.api.presentation.request.AccountLinkRequest;
import com.paydai.api.presentation.request.AccountRequest;
import com.paydai.api.presentation.request.OauthRequest;
import com.paydai.api.presentation.response.JapiResponse;

public interface AccountService {
  JapiResponse createAccount(AccountRequest payload);

  JapiResponse createAccountLink(AccountLinkRequest payload);

  JapiResponse authenticate(OauthRequest payload);

  JapiResponse getStripeAccount(String accountId);

  String serveIndex();
}
