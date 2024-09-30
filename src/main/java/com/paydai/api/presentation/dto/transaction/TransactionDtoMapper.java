package com.paydai.api.presentation.dto.transaction;

import com.paydai.api.domain.model.InvoiceModel;
import com.paydai.api.domain.model.TransactionModel;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class TransactionDtoMapper implements Function<TransactionModel, TransactionRecord> {
  @Override
  public TransactionRecord apply(TransactionModel transactionModel) {
    InvoiceModel invoiceModel = transactionModel.getInvoice();

    double merchantFee = 0.0;

    if (invoiceModel != null) {
      merchantFee = invoiceModel.getSnapshotMerchantFeePercent() * invoiceModel.getAmount();
    }

    assert invoiceModel != null;
    return new TransactionRecord(
      transactionModel.getRevenue(),
      transactionModel.getAmount(),
      transactionModel.getFee(),
      merchantFee,
      transactionModel.getGiver(),
      transactionModel.getReceiver(),
      transactionModel.getTxnType(),
      transactionModel.getEntryType(),
      transactionModel.getPayoutDate(),
      transactionModel.getStripeInvoiceCode(),
      invoiceModel.getInvoiceCode(),
      transactionModel.getStatus(),
      transactionModel.getCreatedAt(),
      transactionModel.getUpdatedAt()
    );
  }
}
