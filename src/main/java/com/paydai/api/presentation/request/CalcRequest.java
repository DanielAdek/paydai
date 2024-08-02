package com.paydai.api.presentation.request;

import com.paydai.api.domain.model.CommSplitScenarioType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalcRequest {
  private double revenue;
  private CommSplitScenarioType scenario;
  private float setterPercent;
  private float closerPercent;
}
