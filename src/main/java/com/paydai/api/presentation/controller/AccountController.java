package com.paydai.api.presentation.controller;

import com.paydai.api.presentation.request.AccountLinkRequest;
import com.paydai.api.presentation.request.AccountRequest;
import com.paydai.api.presentation.request.OauthRequest;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface AccountController {
  ResponseEntity<JapiResponse> createAccount(@RequestBody AccountRequest payload) throws StripeException;
  ResponseEntity<JapiResponse> createAccountLink(@RequestBody AccountLinkRequest payload) throws StripeException;
  ResponseEntity<JapiResponse> authenticate(@RequestBody OauthRequest payload) throws StripeException;
  ResponseEntity<JapiResponse> getStripeAccount(@RequestParam String accountId) throws StripeException;
  String serveIndex();
  ResponseEntity<JapiResponse> getStripeLoginLink() throws StripeException;
}

