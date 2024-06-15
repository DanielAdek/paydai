package com.paydai.api.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "permission_tbl")
public class PermissionModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "permission_id")
  private UUID permissionId;

  @Column
  private String permission;

  @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
  private List<RoleModel> roles;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
