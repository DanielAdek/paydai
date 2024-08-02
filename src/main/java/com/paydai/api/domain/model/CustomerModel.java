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
@Table(name = "customer_tbl")
public class CustomerModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column
  private String name;

  @Column
  private String email;

  private String description;

  private String phone;

  private Boolean setterInvolved;

  @ManyToOne
  @JoinColumn(name = "closer_id")
  private UserModel closer;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserModel creator;

  @ManyToOne
  @JoinColumn(name = "role_id")
  private RoleModel creatorRole;

  @ManyToOne
  @JoinColumn(name = "workspace_id")
  private WorkspaceModel workspace;

  @Column
  @Enumerated(EnumType.STRING)
  private CustomerType stage;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
