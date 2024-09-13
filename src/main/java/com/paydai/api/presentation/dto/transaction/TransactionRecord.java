package com.paydai.api.presentation.dto.transaction;

import com.paydai.api.domain.model.TransactionStatusType;
import com.paydai.api.domain.model.TxnEntryType;
import com.paydai.api.domain.model.TxnType;

import java.time.LocalDateTime;

public record TransactionRecord(
  double revenue,
  double amount,
  double fee,
  String giver,
  String receiver,
  TxnType txnType,
  TxnEntryType entryType,
  LocalDateTime payoutDate,
  String stripeInvoiceCode,
  String invoiceCode,
  TransactionStatusType status,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {
}
