package com.paydai.api.domain.service;

import com.paydai.api.presentation.request.TransferRequest;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.Invoice;

import java.util.UUID;

public interface TransactionService {
  JapiResponse transferToSalesRep(Invoice invoice) throws StripeException;
  JapiResponse directTransferToSalesRep(TransferRequest request) throws StripeException;
  JapiResponse getTransactions(UUID userId, UUID workspaceId);
  JapiResponse getTransactionsMerchantUse(UUID workspaceId);
  JapiResponse getTransactions(UUID userId);
  JapiResponse getTransactionsChart(UUID workspaceId);
  JapiResponse getTransactionOverview(String filter, UUID workspaceId);
  JapiResponse getTransactionOverview(UUID userId);
}
