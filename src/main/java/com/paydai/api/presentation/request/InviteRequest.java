package com.paydai.api.presentation.request;

import com.paydai.api.domain.model.AggregateType;
import com.paydai.api.domain.model.EmailModel;
import com.paydai.api.domain.model.RoleModel;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InviteRequest {
  private String companyEmail;
  private UUID workspaceId;
  private AggregateType aggregate;
  private int interval;
  private String intervalUnit;
  private UUID roleId;
  private Float commission;
}
