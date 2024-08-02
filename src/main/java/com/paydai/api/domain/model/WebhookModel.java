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
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "webhook_logs_tbl")
public class WebhookModel {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  private String url;

  private String event;

  @Column(name = "stripe_request_json", length = 2048)
  private String stripeRequestJson;

  @ManyToOne
  @JoinColumn(name = "invoice_id")
  private InvoiceModel invoiceModel;

  @ManyToOne
  @JoinColumn(name = "payout_ledger_id")
  private PayoutLedgerModel payoutLedgerModel;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
