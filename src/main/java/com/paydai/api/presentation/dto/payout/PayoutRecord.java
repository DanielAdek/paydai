package com.paydai.api.presentation.dto.payout;

import com.paydai.api.domain.model.PayoutStatusType;

import java.time.LocalDateTime;
import java.util.UUID;

public record PayoutRecord(
  double revenue,
  double credit,
  double fee,
  LocalDateTime payoutDate,
  String stripeInvoiceCode,
  String invoiceCode,
  PayoutStatusType status,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {
}
