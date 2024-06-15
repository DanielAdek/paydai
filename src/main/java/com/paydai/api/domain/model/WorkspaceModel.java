package com.paydai.api.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "workspace_tbl")
public class WorkspaceModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID workspaceId;

  @Column
  private String name;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserModel owner;

  @OneToMany(mappedBy = "workspace", fetch = FetchType.LAZY)
  private List<UserWorkspaceModel> userWorkspaces;

  @OneToOne(mappedBy = "workspace", fetch = FetchType.LAZY)
  private EmailModel email;

  @OneToMany(mappedBy = "workspace", fetch = FetchType.LAZY)
  private List<RoleModel> roles;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
