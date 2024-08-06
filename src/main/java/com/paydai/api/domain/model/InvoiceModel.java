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
  //TODO SECTION: INVOICE
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "invoice_code")
  private String invoiceCode;

  @Column
  private String subject;

  @Column
  private double amount;

  @Column
  private int amtSmUnit;

  @Column(name = "due_date")
  private LocalDateTime dueDate;

  @Column
  @Enumerated(EnumType.STRING)
  private InvoiceStatus status;

  @Column
  private String currency;

  //TODO SECTION: SNAPSHOT COMMISSION
  @Column(name = "snapshot_comm_setter_percent")
  private Float snapshotCommSetterPercent;

  @Column(name = "snapshot_setter_fee_percent")
  private Float snapshotSetterFeePercent;

  @Column(name = "snapshot_comm_setter")
  private double snapshotCommSetter;

  @Column(name = "snapshot_comm_setter_net")
  private double snapshotCommSetterNet;

  @Column(name = "snapshot_comm_closer_percent")
  private Float snapshotCommCloserPercent;

  @Column(name = "snapshot_closer_fee_percent")
  private Float snapshotCloserFeePercent;

  @Column(name = "snapshot_comm_closer")
  private double snapshotCommCloser;

  @Column(name = "snapshot_comm_closer_net")
  private double snapshotCommCloserNet;

  @Column(name = "snapshot_merchant_fee_percent")
  private Float snapshotMerchantFeePercent;

  @Column(name = "snapshot_comm_aggregate")
  private AggregateType snapshotCommAggregate;

  @Column(name = "snapshot_comm_interval")
  private int snapshotCommInterval;

  @Column(name = "snapshot_comm_interval_unit")
  private String snapshotCommIntervalUnit;

  @Column(name = "snapshot_application_fee")
  private double applicationFee;

  @Column(name = "snapshot_platform_fee")
  private double platformFee;

  @Column(name = "comm_split_scenario")
  private CommSplitScenarioType commSplitScenario;

  // TODO SECTION: STRIPE DETAILS
  @Column(name = "stripe_invoice_id")
  private String stripeInvoiceId;

  @Column(name = "stripe_invoice_item")
  private String stripeInvoiceItem;

  @Column(name = "stripe_invoice_pdf", length = 512)
  private String stripeInvoicePdf;

  @Column(name = "stripe_invoice_hosted_url", length = 512)
  private String stripeInvoiceHostedUrl;

  @Column(name = "stripe_invoice_details", length = 2048)
  private String stripeInvoiceDetails;

  @Column(name = "stripe_invoice_status")
  private String stripeInvoiceStatus;

  //TODO SECTION: JOINING
  @ManyToOne
  @JoinColumn(name = "customer_id")
  private CustomerModel customer;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private ProductModel product;

  @ManyToOne
  @JoinColumn(name = "workspace_id")
  private WorkspaceModel workspace;

  @ManyToOne
  @JoinColumn(name = "user_workspace_id")
  private UserWorkspaceModel userWorkspace;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
