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
@Table(name = "user_workspace_bridge_tbl")
public class UserWorkspaceModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private Boolean removed;

  @ManyToOne()
  @JoinColumn(name = "user_id", nullable = false)
  private UserModel user;

  @ManyToOne()
  @JoinColumn(name = "workspace_id")
  private WorkspaceModel workspace;

  @ManyToOne()
  @JoinColumn(name = "role_id")
  private RoleModel role;

  @OneToOne()
  @JoinColumn(name = "email_id")
  private EmailModel email;

  @ManyToOne
  @JoinColumn(name = "comm_setting_id")
  private CommissionSettingModel commission;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
