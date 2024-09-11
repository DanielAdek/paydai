package com.paydai.api.presentation.dto.invoice;

import com.paydai.api.domain.model.AggregateType;
import com.paydai.api.domain.model.InvoiceStatus;
import com.paydai.api.presentation.dto.commission.CommissionRecord;

import java.time.LocalDateTime;
import java.util.List;

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
  double amount,
  String productDescription,
  double productUnitPrice,
  float snapshotCommCloserPercent,
  float snapshotCommSetterPercent,
  int snapshotCommInterval,
  String snapshotCommIntervalUnit,
  String salesRep,
  InvoiceStatus status,
  CommissionRecord commissionSplit,
  List<InvolvedManagerDto> involvedManagerDto
) {
}
