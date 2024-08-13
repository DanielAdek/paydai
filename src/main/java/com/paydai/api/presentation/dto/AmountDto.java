package com.paydai.api.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmountDto {
  private double smUnitAmt;
  private double lgUnitAmt;
  private int smUnit;

  public AmountDto getAmountDto(double smUnitAmt, String current) {
   String[] supportedCurrencies = {"eur", "dor", ""};
    return AmountDto.builder().build();
  }
}
