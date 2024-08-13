package com.paydai.api.presentation.controller;

import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface AccountLedgerController {
  ResponseEntity<JapiResponse> getUserAccountLedger() throws StripeException;
}
