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
  private UUID id;

  @Column
  private Double commission;

  @Column
  private AggregateType aggregate;

  @Column(name = "next_paydai")
  private LocalDateTime nextPaydai;

  @Column
  private int interval;

  @Column(name = "interval_unit")
  private String intervalUnit;

  @OneToOne
  @JoinColumn(name = "user_workspace_id")
  private UserWorkspaceModel userWorkspace;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}