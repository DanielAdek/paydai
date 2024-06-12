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
@Table(name = "passwords_tbl")
public class PasswordModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID passwordId;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserModel user;

  @OneToOne
  @JoinColumn(name = "email_id")
  private EmailModel email;

  @OneToOne
  @JoinColumn(name = "workspace_id")
  private WorkspaceModel workspace;

  @Column(nullable = false)
  private String passwordHash;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
