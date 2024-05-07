package com.paydai.api.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "_auth")
public class AuthModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "auth_id")
  public UUID authId;
}
