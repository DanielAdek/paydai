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
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invite_tbl")
public class InviteModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column
  private String companyEmail;

  @Column(name = "invite_code")
  private String inviteCode;

  @Column
  private Float commission;

  @Column
  @Enumerated(EnumType.STRING)
  private AggregateType aggregate;

  @Column
  @Enumerated(EnumType.STRING)
  private PositionType position;

  @ElementCollection
  @CollectionTable(name = "assigned_team_members", joinColumns = @JoinColumn(name = "invite_id"))
  @Column(name = "selected_sales_rep")
  private List<UUID> selectedSalesRep;

  @Column
  private int interval;

  @Column(name = "interval_unit")
  private String intervalUnit;

  @ManyToOne
  @JoinColumn(name = "role_id")
  private RoleModel role;

  @ManyToOne
  @JoinColumn(name = "workspace_id")
  private WorkspaceModel workspace;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
