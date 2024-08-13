package com.paydai.api.presentation.dto.payout;

import com.paydai.api.domain.model.PayoutLedgerModel;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PayoutDtoMapper implements Function<PayoutLedgerModel, PayoutRecord> {
  @Override
  public PayoutRecord apply(PayoutLedgerModel payoutLedgerModel) {
    return new PayoutRecord(
      payoutLedgerModel.getRevenue(),
      payoutLedgerModel.getCredit(),
      payoutLedgerModel.getFee(),
      payoutLedgerModel.getPayoutDate(),
      payoutLedgerModel.getStripeInvoiceCode(),
      payoutLedgerModel.getInvoice().getInvoiceCode(),
      payoutLedgerModel.getStatus(),
      payoutLedgerModel.getCreatedAt(),
      payoutLedgerModel.getUpdatedAt()
    );
  }
}
