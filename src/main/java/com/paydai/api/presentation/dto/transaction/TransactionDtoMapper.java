package com.paydai.api.presentation.dto.transaction;

import com.paydai.api.domain.model.TransactionModel;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class TransactionDtoMapper implements Function<TransactionModel, TransactionRecord> {
  @Override
  public TransactionRecord apply(TransactionModel transactionModel) {
    return new TransactionRecord(
      transactionModel.getRevenue(),
      transactionModel.getAmount(),
      transactionModel.getFee(),
      transactionModel.getGiver(),
      transactionModel.getReceiver(),
      transactionModel.getTxnType(),
      transactionModel.getEntryType(),
      transactionModel.getPayoutDate(),
      transactionModel.getStripeInvoiceCode(),
      transactionModel.getInvoice().getInvoiceCode(),
      transactionModel.getStatus(),
      transactionModel.getCreatedAt(),
      transactionModel.getUpdatedAt()
    );
  }
}
