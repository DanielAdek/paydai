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
@Table(name = "invoice_manager_involved_tbl")
public class InvoiceManagerModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "snapshot_comm_manager_percent")
  private Float snapshotCommManagerPercent;

  @Column(name = "snapshot_manager_fee_percent")
  private Float snapshotManagerFeePercent;  // if the future requires paydai fee from manager

  @Column(name = "snapshot_comm_manager")
  private double snapshotCommManager;

  @Column(name = "snapshot_comm_manager_net")
  private double snapshotCommManagerNet; // if snapshotManagerFeePercent applies then this amount will be less than snapshotCommManager

  @ManyToOne
  @JoinColumn(name = "manager_id")
  private UserModel manager;

  @ManyToOne
  @JoinColumn(name = "invoice_id")
  private InvoiceModel invoice;

  @ManyToOne
  @JoinColumn(name = "user_workspace_id")
  private UserWorkspaceModel userWorkspace;

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
