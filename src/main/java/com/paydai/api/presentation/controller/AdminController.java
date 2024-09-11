package com.paydai.api.presentation.controller;

import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface AdminController {
  ResponseEntity<JapiResponse> getAllUsersStripeAccounts() throws StripeException;
  ResponseEntity<JapiResponse> removeConnectedStripeAccount(@RequestBody List<String> connectedAccounts) throws StripeException;
}
