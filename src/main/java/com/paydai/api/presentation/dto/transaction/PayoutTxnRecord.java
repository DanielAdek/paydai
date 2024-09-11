package com.paydai.api.presentation.dto.transaction;

import com.stripe.param.PayoutCreateParams;

public record PayoutTxnRecord (
  double amount,
  double fee,
  double unit,
  String currency,
  PayoutCreateParams.Method type,
  PayoutCreateParams.SourceType sourceType,
  String destinationId
) {
}
