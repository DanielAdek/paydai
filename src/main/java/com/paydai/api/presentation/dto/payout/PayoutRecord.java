package com.paydai.api.presentation.dto.payout;

import com.paydai.api.domain.model.PayoutStatusType;

import java.time.LocalDateTime;

public record PayoutRecord(
  double amount,
  double fee,
  LocalDateTime payoutDate,
  String stripeInvoiceCode,
  String invoiceCode,
  PayoutStatusType status
) {
}
