package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.PayoutLedgerModel;

public interface PayoutLedgerRepository {
  PayoutLedgerModel save(PayoutLedgerModel buildPayout);
  PayoutLedgerModel findPayoutByStripeInvoiceId(String stripeInvoiceCod);
}
