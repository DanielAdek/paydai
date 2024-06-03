package com.paydai.api.presentation.controller;

import com.paydai.api.presentation.request.AccountLinkRequest;
import com.paydai.api.presentation.request.AccountRequest;
import com.paydai.api.presentation.response.JapiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface AccountController {
  ResponseEntity<JapiResponse> createAccount(@RequestBody AccountRequest payload);

  ResponseEntity<JapiResponse> createAccountLink(@RequestBody AccountLinkRequest payload);

  String serveIndex();
}

