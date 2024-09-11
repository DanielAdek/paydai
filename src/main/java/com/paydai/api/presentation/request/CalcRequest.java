package com.paydai.api.presentation.request;

import com.paydai.api.domain.model.CommSplitScenarioType;
import com.paydai.api.domain.model.TeamModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalcRequest {
  private double revenue;
  private CommSplitScenarioType scenario;
  private float setterPercent;
  private float closerPercent;
  private List<TeamModel> closerManager;
  private List<TeamModel> setterManager;
}
