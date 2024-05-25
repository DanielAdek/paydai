package com.paydai.api.domain.service;

import com.paydai.api.presentation.dto.StripeAccountDto;
import com.paydai.api.presentation.response.JapiResponse;

public interface AccountService {
  JapiResponse createAccount();

  JapiResponse createAccountLink(StripeAccountDto stripeAccountDto);

  String serveIndex();
}
