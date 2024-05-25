package com.paydai.api.presentation.controller;

import com.paydai.api.presentation.dto.StripeAccountDto;
import com.paydai.api.presentation.response.JapiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface AccountController {
  ResponseEntity<JapiResponse> createAccount();

  ResponseEntity<JapiResponse> createAccountLink(@RequestBody StripeAccountDto stripeAccountDto);

  String serveIndex();
}

