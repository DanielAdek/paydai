package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.PayoutLedgerModel;

import java.util.List;
import java.util.UUID;

public interface PayoutLedgerRepository {
  PayoutLedgerModel save(PayoutLedgerModel buildPayout);
  PayoutLedgerModel findPayoutByStripeInvoiceId(String stripeInvoiceCod);
  List<PayoutLedgerModel> findPayoutTransactions(UUID userId, UUID workspaceId);
  List<PayoutLedgerModel> findPayoutTransactions(UUID userId);
}
