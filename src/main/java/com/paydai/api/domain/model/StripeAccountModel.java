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
public class StripeAccountModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "stripe_account_id")
  private UUID stripeAccountId;

  @Column(name = "personal_email")
  private String personalEmail;

  @Column(name = "user_id")
  private UUID userId;

  @Column(name = "stripe_id")
  private String stripeId;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
