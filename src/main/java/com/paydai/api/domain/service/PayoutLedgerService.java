package com.paydai.api.domain.service;

import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;

import java.util.UUID;

public interface PayoutLedgerService {
  JapiResponse transferToSalesRep(String stripeInvoiceCode) throws StripeException;
  JapiResponse getPayoutLedgerTransactions(UUID userId, UUID workspaceId);
  JapiResponse getPayoutLedgerTransactions(UUID userId);
  JapiResponse getTransactionOverview(UUID userId, UUID workspaceId);
  JapiResponse getTransactionOverview(UUID userId);
}
