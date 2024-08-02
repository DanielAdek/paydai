package com.paydai.api.presentation.dto.invoice;

import com.paydai.api.domain.model.AggregateType;
import com.paydai.api.domain.model.InvoiceModel;
import com.paydai.api.domain.model.InvoiceStatus;
import com.paydai.api.presentation.dto.commission.CommissionRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {
  private String invoiceCode;
  private String subject;
  private String currency;
  private LocalDateTime dueDate;
  private String stripeInvoiceId;
  private String stripeInvoiceItem;
  private String stripeInvoicePdf;
  private String stripeInvoiceHostedUrl;
  private LocalDateTime createdAt;
  private String customerName;
  private String customerEmail;
  private String productName;
  private int productQty;
  private double amount;
  private String productDescription;
  private double productUnitPrice;
  private float snapshotCommCloserPercent;
  private float snapshotCommSetterPercent;
  private AggregateType snapshotCommAggregate;
  private int snapshotCommInterval;
  private String snapshotCommIntervalUnit;
  private String salesRep;
  private InvoiceStatus status;
  private CommissionRecord commissionSplit;

  public static InvoiceDto getInvoiceDto(InvoiceModel invoiceModel, CommissionRecord commissionSplit) {
    return new InvoiceDto(
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
      invoiceModel.getSnapshotCommCloserPercent(),
      invoiceModel.getSnapshotCommSetterPercent(),
      invoiceModel.getSnapshotCommAggregate(),
      invoiceModel.getSnapshotCommInterval(),
      invoiceModel.getSnapshotCommIntervalUnit(),
      invoiceModel.getUserWorkspace().getUser().getFirstName(),
      invoiceModel.getStatus(),
      commissionSplit
    );
  }
}
