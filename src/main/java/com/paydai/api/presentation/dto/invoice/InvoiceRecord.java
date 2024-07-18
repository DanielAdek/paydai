package com.paydai.api.presentation.dto.invoice;

import com.paydai.api.domain.model.AggregateType;
import com.paydai.api.domain.model.InvoiceStatus;

import java.time.LocalDateTime;

public record InvoiceRecord(
  String invoiceCode,
  String subject,
  String currency,
  LocalDateTime dueDate,
  String stripeInvoiceId,
  String stripeInvoiceItem,
  String stripeInvoicePdf,
  String stripeInvoiceHostedUrl,
  LocalDateTime createdAt,
  String customerName,
  String customerEmail,
  String productName,
  int productQty,
  Double amount,
  String productDescription,
  Double productUnitPrice,
  Double snapshotCommPercent,
  AggregateType snapshotCommAggregate,
  int snapshotCommInterval,
  String snapshotCommIntervalUnit,
  String salesRep,
  InvoiceStatus status
) {
}
