package com.paydai.api.presentation.dto.invoice;

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
  String productDescription,
  Double productUnitPrice
) {
}
