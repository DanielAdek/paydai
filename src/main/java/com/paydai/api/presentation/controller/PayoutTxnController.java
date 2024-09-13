package com.paydai.api.presentation.controller;

import com.paydai.api.presentation.request.PayoutRequest;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface PayoutTxnController {
  ResponseEntity<JapiResponse> createPayoutTransaction(@RequestBody PayoutRequest payload) throws StripeException;
  ResponseEntity<JapiResponse> getPayoutTransactions(@RequestParam UUID userId);
  ResponseEntity<JapiResponse> getPayoutTransactions(@RequestParam UUID userId, @RequestParam UUID workspaceId);
  ResponseEntity<JapiResponse> topUpBalance(@RequestParam double amount, @RequestParam String currency) throws StripeException;
}
