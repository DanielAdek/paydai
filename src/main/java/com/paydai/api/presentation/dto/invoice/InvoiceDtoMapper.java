package com.paydai.api.presentation.dto.invoice;

import com.paydai.api.domain.model.InvoiceModel;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class InvoiceDtoMapper implements Function<InvoiceModel, InvoiceRecord> {

  @Override
  public InvoiceRecord apply(InvoiceModel invoiceModel) {
    return new InvoiceRecord(
      invoiceModel.getInvoiceCode(),
      invoiceModel.getSubject(),
      invoiceModel.getCurrency(),
      invoiceModel.getDueDate(),
      invoiceModel.getStripeInvoiceId(),
      invoiceModel.getStripeInvoiceItem(),
      invoiceModel.getStripeInvoicePdf(),
      invoiceModel.getStripeInvoiceHostedUrl(),
      invoiceModel.getCreatedAt(),
      invoiceModel.getCustomer().getName(),
      invoiceModel.getCustomer().getEmail(),
      invoiceModel.getProduct().getItem(),
      invoiceModel.getProduct().getQty(),
      invoiceModel.getProduct().getUnitPrice(),
      invoiceModel.getProduct().getDescription(),
      invoiceModel.getProduct().getUnitPrice(),
      invoiceModel.getSnapshotCommPercent(),
      invoiceModel.getSnapshotCommAggregate(),
      invoiceModel.getSnapshotCommInterval(),
      invoiceModel.getSnapshotCommIntervalUnit(),
      invoiceModel.getUserWorkspace().getUser().getFirstName(),
      invoiceModel.getStatus()
    );
  }
}
