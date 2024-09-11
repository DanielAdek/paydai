package com.paydai.api.presentation.dto.commission;

import com.paydai.api.domain.model.InvoiceManagerModel;

import java.util.List;

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
  double paydaiApplicationFee,
  List<InvoiceManagerModel> closerManagersCommissions,
  List<InvoiceManagerModel> setterManagersCommissions
) {
}
