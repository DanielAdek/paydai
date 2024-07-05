package com.paydai.api.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

  @Enumerated(EnumType.STRING)
  @Column(name = "email_type")
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
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
