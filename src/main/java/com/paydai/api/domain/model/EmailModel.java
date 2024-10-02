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
  private UUID id;

  @Column
  private String email;

  @Column
  private String passwordHash;

  private boolean access;

  @Enumerated(EnumType.STRING)
  @Column(name = "email_type")
  private EmailType emailType; // todo use string instead of enum

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserModel user;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
