package com.paydai.api.presentation.dto.invoice;

import com.paydai.api.domain.model.AggregateType;
import com.paydai.api.domain.model.InvoiceManagerModel;
import com.paydai.api.domain.model.InvoiceModel;
import com.paydai.api.domain.model.InvoiceStatus;
import com.paydai.api.presentation.dto.commission.CommissionRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
  private int snapshotCommInterval;
  private String snapshotCommIntervalUnit;
  private String salesRep;
  private InvoiceStatus status;
  private CommissionRecord commissionSplit;
  private List<InvolvedManagerDto> involvedManagers;


  public static InvoiceDto getInvoiceDto(InvoiceModel invoiceModel, CommissionRecord commissionSplit, List<InvoiceManagerModel> involvedManagers) {
    List<InvolvedManagerDto> involvedManagerDtos = new ArrayList<>();
    if (involvedManagers != null && !involvedManagers.isEmpty()) {
      involvedManagerDtos = involvedManagers.stream()
        .map(InvolvedManagerDto::getInvolvedManagerDto)
        .collect(Collectors.toList());
    }
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
      invoiceModel.getAmount(),
      invoiceModel.getProduct().getDescription(),
      invoiceModel.getProduct().getUnitPrice(),
      invoiceModel.getSnapshotCommCloserPercent(),
      invoiceModel.getSnapshotCommSetterPercent(),
      invoiceModel.getSnapshotCommInterval(),
      invoiceModel.getSnapshotCommIntervalUnit(),
      invoiceModel.getUserWorkspace().getUser().getFirstName(),
      invoiceModel.getStatus(),
      commissionSplit,
      involvedManagerDtos
    );
  }
}
