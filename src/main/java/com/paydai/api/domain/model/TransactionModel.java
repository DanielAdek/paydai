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
@Table(name = "transaction_ledger_tbl")
public class TransactionModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private double revenue;

  private double fee;

  private double amount;

  private String currency;

  private String remark;

  private String receiver;

  private String giver;

  @Enumerated(EnumType.STRING)
  private TxnEntryType entryType;

  @Enumerated(EnumType.STRING)
  private TxnType txnType;

  @Column(name = "stripe_invoice_code")
  private String stripeInvoiceCode;

  @Column(name = "payout_date")
  @CreationTimestamp
  private LocalDateTime payoutDate;

  @Enumerated(EnumType.STRING)
  private TxnStatusType status;

  @ManyToOne
  @JoinColumn(name = "invoice_id")
  private InvoiceModel invoice;

  @ManyToOne
  @JoinColumn(name = "workspace_id")
  private WorkspaceModel workspace;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserModel user;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
