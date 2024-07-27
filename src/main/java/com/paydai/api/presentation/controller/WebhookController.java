package com.paydai.api.presentation.controller;

import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface WebhookController {
  ResponseEntity handleConnectEvents(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signature);
  ResponseEntity handleAccountEvents(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signature);
}
