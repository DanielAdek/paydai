package com.paydai.api.domain.model;

import com.stripe.param.PayoutCreateParams;
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
@Table(name = "payout_txn_tbl")
public class PayoutTxnModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private double amount;

  private double fee;

  private double unit;

  private String currency;

  private PayoutCreateParams.Method type;

  private PayoutCreateParams.SourceType sourceType;

  private String destinationId;

  @Column(name = "payout_initiated")
  @CreationTimestamp
  private LocalDateTime payoutInitiatedDate;

  @Column(name = "payout_settled")
  @CreationTimestamp
  private LocalDateTime payoutSettleDate;

  @Enumerated(EnumType.STRING)
  private TxnStatusType status;

  @ManyToOne
  @JoinColumn(name = "workspace_id")
  private WorkspaceModel workspace;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserModel user;

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
