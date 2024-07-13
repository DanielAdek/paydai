package com.paydai.api.presentation.controller;

import com.paydai.api.presentation.request.AccountLinkRequest;
import com.paydai.api.presentation.request.AccountRequest;
import com.paydai.api.presentation.request.OauthRequest;
import com.paydai.api.presentation.response.JapiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface AccountController {
  ResponseEntity<JapiResponse> createAccount(@RequestBody AccountRequest payload);

  ResponseEntity<JapiResponse> createAccountLink(@RequestBody AccountLinkRequest payload);

  ResponseEntity<JapiResponse> authenticate(@RequestBody OauthRequest payload);

  ResponseEntity<JapiResponse> getStripeAccount(@RequestParam String accountId);

  String serveIndex();
}

