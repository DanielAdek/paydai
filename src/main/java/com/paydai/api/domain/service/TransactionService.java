package com.paydai.api.domain.service;

import com.paydai.api.presentation.request.TransferRequest;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;

import java.util.UUID;

public interface TransactionService {
  JapiResponse transferToSalesRep(String stripeInvoiceCode) throws StripeException;
  JapiResponse directTransferToSalesRep(TransferRequest request) throws StripeException;
  JapiResponse getTransactions(UUID userId, UUID workspaceId);
  JapiResponse getTransactionsMerchantUse(UUID workspaceId);
  JapiResponse getTransactions(UUID userId);
  JapiResponse getTransactionOverview(UUID userId, UUID workspaceId);
  JapiResponse getTransactionOverview(UUID userId);
}
