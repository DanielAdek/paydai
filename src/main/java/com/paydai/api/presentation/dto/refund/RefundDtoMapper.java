package com.paydai.api.presentation.dto.refund;

import com.paydai.api.domain.model.RefundModel;
import com.paydai.api.domain.model.UserModel;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Function;

@Service
public class RefundDtoMapper implements Function<RefundModel, RefundRecord> {
  @Override
  public RefundRecord apply(RefundModel refundModel) {
    UserModel debtor = refundModel.getUser() != null ? refundModel.getUser() : null;
    assert debtor != null;
    return new RefundRecord(
      refundModel.getAmount(),
      refundModel.getTotalPaid(),
      refundModel.getReason(),
       debtor.getFirstName() + " " + debtor.getLastName(),
      debtor.getId(),
      refundModel.getStatus(),
      refundModel.getInvoice() != null ? refundModel.getInvoice().getInvoiceCode() : null
    );
  }
}
