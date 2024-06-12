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
  private UUID userWorkspaceId;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserModel user;

  @ManyToOne
  @JoinColumn(name = "workspace_id")
  private WorkspaceModel workspaceId;

  @OneToOne
  @JoinColumn(name = "role_id")
  private RoleModel role;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
