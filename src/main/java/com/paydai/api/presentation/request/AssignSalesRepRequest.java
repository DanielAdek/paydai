package com.paydai.api.presentation.request;

import com.paydai.api.domain.model.AggregateCustomRep;
import com.paydai.api.domain.model.AggregateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignSalesRepRequest {
  private AggregateType aggregate;
  private List<UUID> selectedSalesRep;
  private AggregateCustomRep aggregateCustomRep;
}
