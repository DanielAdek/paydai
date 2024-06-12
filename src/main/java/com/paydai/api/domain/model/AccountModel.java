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
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stripe_account_tbl")
public class AccountModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "stripe_account_id")
  public UUID stripeAccountId;

  @CreationTimestamp
  public LocalDateTime createdAt;

  @UpdateTimestamp
  public LocalDateTime updatedAt;
}
