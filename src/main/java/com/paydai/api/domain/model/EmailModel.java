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
@Table(name = "email_tbl")
public class EmailModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID emailId;

  @Column
  private String email;

  @Column
  @Enumerated(EnumType.STRING)
  private EmailType emailType;

  @OneToOne
  @JoinColumn(name = "workspace_id")
  private WorkspaceModel workspace;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserModel user;

  @OneToOne(mappedBy = "email", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private PasswordModel password;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
