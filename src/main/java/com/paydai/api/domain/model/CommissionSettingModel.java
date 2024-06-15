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
@Table(name = "commission_setting_tbl")
public class CommissionSettingModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "commission_id")
  private UUID comSettingId;

  @Column
  private Double commission;

  @Column
  private String aggregate;

  @Column(name = "next_paydai")
  private LocalDateTime nextPaydai;

  @Column
  private int interval;

  @Column
  private String duration;

  @OneToOne
  @JoinColumn(name = "role_id")
  private RoleModel role;

  @OneToOne
  @JoinColumn(name = "email_id")
  private EmailModel emailId;

  @Column(name = "workspace_id")
  private UUID workspaceId;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}