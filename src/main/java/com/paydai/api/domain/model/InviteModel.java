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
@Table(name = "invite_tbl")
public class InviteModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "invite_id")
  private UUID inviteId;

  @Column
  private String companyEmail;

  @Column(name = "invite_code")
  private String inviteCode;

  @Column
  private Double commission;

  @Column
  private AggregateType aggregate;

  @Column
  private int interval;

  @Column
  private String duration;

  @OneToOne
  @JoinColumn(name = "role_id")
  private RoleModel role;

  @OneToOne
  @JoinColumn(name = "workspace_id")
  private WorkspaceModel workspace;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
