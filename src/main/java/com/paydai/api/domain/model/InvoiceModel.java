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
  private String currency;

  @Column(name = "merchant_fee")
  private Double merchantFee; // 1.5 this should take value at point of invoice create from user table

  @Column(name = "sales_rep_fee")
  private String saleRepFee; //

  @OneToOne
  @JoinColumn(name = "user_workspace_id")
  private UserWorkspaceModel userWorkspace;

  @Column(name = "stripe_invoice_id")
  private String stripeInvoiceId;

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

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}