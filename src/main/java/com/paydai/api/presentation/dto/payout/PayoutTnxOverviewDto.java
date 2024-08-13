package com.paydai.api.presentation.dto.payout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutTnxOverviewDto {
  private double totalRevenues;
  private double totalCredits;
  private double totalFees;
}
