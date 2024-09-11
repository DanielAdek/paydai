package com.paydai.api.presentation.dto.transaction;

import com.paydai.api.domain.model.PayoutTxnModel;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PayoutTxnDtoMapper implements Function<PayoutTxnModel, PayoutTxnRecord> {
  @Override
  public PayoutTxnRecord apply(PayoutTxnModel payoutTxnModel) {
    return new PayoutTxnRecord(
      payoutTxnModel.getAmount(),
      payoutTxnModel.getFee(),
      payoutTxnModel.getUnit(),
      payoutTxnModel.getCurrency(),
      payoutTxnModel.getType(),
      payoutTxnModel.getSourceType(),
      payoutTxnModel.getDestinationId()
    );
  }
}
