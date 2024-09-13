package com.paydai.api.presentation.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionOverviewDto {
  private double totalRevenues;
  private double totalCredits;
  private double totalFees;
}
