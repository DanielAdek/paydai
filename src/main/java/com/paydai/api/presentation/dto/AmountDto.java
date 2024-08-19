package com.paydai.api.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmountDto {
  private double smUnitAmt;
  private double lgUnitAmt;
  private int smUnit;

  private static final List<String> UNIT_CURRENCIES = List.of("eur", "dor", "gbp");
  private static final List<String> NO_UNIT_CURRENCIES = List.of("jpy");

  public static AmountDto getAmountDto(double _smUnitAmt, String current) {
    String normalizedCurrent = current.toLowerCase();

    if (UNIT_CURRENCIES.contains(normalizedCurrent)) {
      return new AmountDto(_smUnitAmt, _smUnitAmt * 100, 100);
    }

    if (NO_UNIT_CURRENCIES.contains(normalizedCurrent)) {
      return new AmountDto(_smUnitAmt, _smUnitAmt, 1);
    }

    return AmountDto.builder().smUnit(100).lgUnitAmt(_smUnitAmt * 100).smUnitAmt(_smUnitAmt).build();
  }
}
