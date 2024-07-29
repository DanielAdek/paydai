package com.paydai.api.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoice_tbl")
public class InvoiceModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "invoice_code")
  private String invoiceCode;

  @Column
  private String subject;

  @Column(name = "due_date")
  private LocalDateTime dueDate;

  @Column
  @Enumerated(EnumType.STRING)
  private InvoiceStatus status;

  @Column
  private String currency;

  @Column(name = "merchant_fee")
  private Float merchantFee; // 1.5 this should take value at point of invoice create from user table

  @Column(name = "sales_rep_fee")
  private Float salesRepFee; // 0.5 this should take value at point of invoice create from user table

  @Column(name = "snapshot_comm_setter_percent")
  private Float snapshotCommSetterPercent;

  @Column(name = "snapshot_comm_closer_percent")
  private Float snapshotCommCloserPercent;

  @Column(name = "snapshot_comm_aggregate")
  private AggregateType snapshotCommAggregate;

  @Column(name = "snapshot_comm_interval")
  private int snapshotCommInterval;

  @Column(name = "snapshot_comm_interval_unit")
  private String snapshotCommIntervalUnit;

  @ManyToOne
  @JoinColumn(name = "workspace_id")
  private WorkspaceModel workspace;

  @ManyToOne
  @JoinColumn(name = "user_workspace_id")
  private UserWorkspaceModel userWorkspace;

  @Column(name = "stripe_invoice_id")
  private String stripeInvoiceId;

  @Column(name = "stripe_invoice_item")
  private String stripeInvoiceItem;

  @Column(name = "stripe_invoice_pdf")
  private String stripeInvoicePdf;

  @Column(name = "stripe_invoice_hosted_url")
  private String stripeInvoiceHostedUrl;

  @Column(name = "stripe_invoice_details")
  private String stripeInvoiceDetails; // String(Object) raw data; upon created

  @Column(name = "stripe_invoice_status")
  private Boolean stripeInvoiceStatus;

  @Column(name = "calculated_commission")
  private Double calculatedComm; // json

  @Column(name = "stripe_invoice_webhook")
  private String stripeInvoiceWebhook; // raw upon stripe return

  @ManyToOne
  @JoinColumn(name = "customer_id")
  private CustomerModel customer;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private ProductModel product;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
