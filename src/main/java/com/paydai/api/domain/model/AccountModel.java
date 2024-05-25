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
@Table(name = "tbl_account")
public class AccountModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "account_id")
  public UUID accountId;
}
