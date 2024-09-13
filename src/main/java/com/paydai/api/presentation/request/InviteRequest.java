package com.paydai.api.presentation.request;

import com.paydai.api.domain.model.AggregateCustomRep;
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
import java.util.List;
import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InviteRequest {
  private UUID roleId;
  private int interval;
  private Float commission;
  private UUID workspaceId;
  private String companyEmail;
  private String intervalUnit;
  private AggregateType aggregate;
  private List<UUID> selectedSalesRep;
  private AggregateCustomRep aggregateCustomRep;
}
