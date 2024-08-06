package com.paydai.api.presentation.dto.commission;

public record CommissionRecord (
  double paydaiFeeMerchant,
  float paydaiFeeSetterPercent,
  float paydaiFeeCloserPercent,
  float paydaiFeeMerchantPercent,
  double paydaiFeeCloserOnly,
  double closerCommission,
  double closerOnlyNet,
  double paydaiTotalComm,
  double paydaiFeeCloser,
  double paydaiFeeSetter,
  double setterCommission,
  double setterNet,
  double closerNet,
  double paydaiApplicationFee
) {
}
