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
@Table(name = "workspace_tbl")
public class WorkspaceModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID workspaceId;

  @Column
  private String name;

  @OneToMany(mappedBy = "workspace")
  private List<UserWorkspaceModel> userWorkspaces;

  @OneToMany(mappedBy = "password")
  private List<PasswordModel> passwords;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
