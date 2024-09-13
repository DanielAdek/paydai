package com.paydai.api.presentation.dto.refund;

import com.paydai.api.domain.model.RefundStatus;

import java.util.UUID;

public record RefundRecord (
  double amount,
  double totalPaid,
  String reason,
  String debtor,
  UUID debtorId,
  RefundStatus status,
  String invoiceId
) {
}
