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
@Table(name = "account_ledger_tbl")
public class AccountLedgerModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private double balance;

  private String currency;

  private double liability;

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
