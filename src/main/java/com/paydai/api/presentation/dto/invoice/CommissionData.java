package com.paydai.api.presentation.dto.invoice;

import com.paydai.api.domain.model.TeamModel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CommissionData {
  private List<TeamModel> managerTeams;
  private float setterCommissionPercent;
}
