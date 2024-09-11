package com.paydai.api.domain.service;

import com.stripe.exception.StripeException;
import com.paydai.api.presentation.request.PayoutRequest;
import com.paydai.api.presentation.response.JapiResponse;

import java.util.UUID;

public interface PayoutTxnService {
  JapiResponse createPayout(PayoutRequest payoutRequest) throws StripeException;
  JapiResponse getUserPayoutTransactions(UUID userId);
  JapiResponse getUserPayoutTransactions(UUID userId, UUID workspaceId);
  JapiResponse topUpAccount(double amount, String currency) throws StripeException;
}
