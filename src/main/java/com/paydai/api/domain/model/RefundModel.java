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
@Table(name = "refund_tbl")
public class RefundModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private double amount;

  @Column(name = "total_paid")
  private double totalPaid;

  private String reason;

  @Enumerated(EnumType.STRING) // todo please use varchar on this column
  private RefundStatus status;

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
