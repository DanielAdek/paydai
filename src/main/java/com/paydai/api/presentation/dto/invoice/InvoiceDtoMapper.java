package com.paydai.api.presentation.dto.invoice;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class InvoiceDtoMapper implements Function<InvoiceDto, InvoiceRecord> {

  @Override
  public InvoiceRecord apply(InvoiceDto invoiceDto) {
    return new InvoiceRecord(
      invoiceDto.getInvoiceCode(),
      invoiceDto.getSubject(),
      invoiceDto.getCurrency(),
      invoiceDto.getDueDate(),
      invoiceDto.getStripeInvoiceId(),
      invoiceDto.getStripeInvoiceItem(),
      invoiceDto.getStripeInvoicePdf(),
      invoiceDto.getStripeInvoiceHostedUrl(),
      invoiceDto.getCreatedAt(),
      invoiceDto.getCustomerName(),
      invoiceDto.getCustomerEmail(),
      invoiceDto.getProductName(),
      invoiceDto.getProductQty(),
      invoiceDto.getAmount(),
      invoiceDto.getProductDescription(),
      invoiceDto.getProductUnitPrice(),
      invoiceDto.getSnapshotCommCloserPercent(),
      invoiceDto.getSnapshotCommSetterPercent(),
      invoiceDto.getSnapshotCommInterval(),
      invoiceDto.getSnapshotCommIntervalUnit(),
      invoiceDto.getSalesRep(),
      invoiceDto.getStatus(),
      invoiceDto.getCommissionSplit(),
      invoiceDto.getInvolvedManagers()
    );
  }
}
