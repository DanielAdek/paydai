package com.paydai.api.presentation.dto.commission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionDto {
  private double paydaiFeeMerchant;
  private float paydaiFeeSetterPercent;
  private float paydaiFeeCloserPercent;
  private float paydaiFeeMerchantPercent;
  private double paydaiFeeCloserOnly;
  private double closerCommission;
  private double closerOnlyNet;
  private double paydaiTotalComm;
  private double paydaiFeeCloser;
  private double paydaiFeeSetter;
  private double setterCommission;
  private double setterNet;
  private double closerNet;
  private double paydaiApplicationFee;
}
