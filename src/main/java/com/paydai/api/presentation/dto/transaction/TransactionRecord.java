package com.paydai.api.presentation.dto.transaction;

import com.paydai.api.domain.model.TxnStatusType;
import com.paydai.api.domain.model.TxnEntryType;
import com.paydai.api.domain.model.TxnType;

import java.time.LocalDateTime;

public record TransactionRecord(
  double revenue,
  double amount,
  double fee,
  double merchantFee,
  String giver,
  String receiver,
  TxnType txnType,
  TxnEntryType entryType,
  LocalDateTime payoutDate,
  String stripeInvoiceCode,
  String invoiceCode,
  TxnStatusType status,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {
}
