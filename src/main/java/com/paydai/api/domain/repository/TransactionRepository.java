package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.TransactionModel;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository {
  TransactionModel save(TransactionModel buildTransaction);
  TransactionModel findTransactionByStripeInvoiceId(String stripeInvoiceCod);
  List<TransactionModel> findTransactions(UUID userId, UUID workspaceId);
  List<TransactionModel> findTransactions(UUID userId);
  List<TransactionModel> findTransactionsMerchant(UUID workspaceId);
}
