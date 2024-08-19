package com.paydai.api.domain.service;

import com.paydai.api.presentation.request.AccountLinkRequest;
import com.paydai.api.presentation.request.AccountRequest;
import com.paydai.api.presentation.request.OauthRequest;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;

public interface AccountService {
  JapiResponse createAccount(AccountRequest payload) throws StripeException;

  JapiResponse createAccountLink(AccountLinkRequest payload) throws StripeException;
  JapiResponse getStripeLoginLink() throws StripeException;

  JapiResponse authenticate(OauthRequest payload) throws StripeException;

  JapiResponse getStripeAccount(String accountId) throws StripeException;

  String serveIndex();
}
