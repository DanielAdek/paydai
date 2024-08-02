package com.paydai.api.presentation.dto.commission;

public record CommissionRecord (
  double paydaiFeeMerchant,
  double paydaiFeeCloserOnly,
  double closerCommission,
  double closerOnlyNet,
  double paydaiTotalComm,
  double paydaiFeeCloser,
  double paydaiFeeSetter,
  double setterCommission,
  double setterNet,
  double closerNet
) {
}
