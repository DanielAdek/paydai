package com.paydai.api.application;

import com.paydai.api.domain.model.CustomerModel;
import com.paydai.api.domain.model.InvoiceModel;
import com.paydai.api.domain.model.PayoutLedgerModel;
import com.paydai.api.domain.model.PayoutStatusType;
import com.paydai.api.domain.repository.AccountLedgerRepository;
import com.paydai.api.domain.repository.InvoiceRepository;
import com.paydai.api.domain.repository.PayoutLedgerRepository;
import com.paydai.api.domain.service.PayoutLedgerService;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.Transfer;
import com.stripe.param.TransferCreateParams;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutLedgerServiceImpl implements PayoutLedgerService {
  private final PayoutLedgerRepository repository;
  private final InvoiceRepository invoiceRepository;
  private final AccountLedgerRepository accountLedgerRepository;

  @Override
  public JapiResponse transferToSalesRep(String stripeInvoiceCode) throws StripeException {
    try {
      InvoiceModel invoiceModel = invoiceRepository.findByStripeInvoiceCode(stripeInvoiceCode);

      if (invoiceModel == null) {
        log.error("The invoice code is invalid");
        return JapiResponse.success(null);
      }

      CustomerModel customerModel = invoiceModel.getCustomer();

      // TRANSFER TO CLOSER
      TransferCreateParams closerTransferParams = TransferCreateParams.builder()
        .setAmount(Double.valueOf(invoiceModel.getSnapshotCommCloserNet()).longValue())
        .setCurrency(invoiceModel.getCurrency())
        .setDestination(customerModel.getCloser().getStripeId())
        .build();

      Transfer closerTransfer = Transfer.create(closerTransferParams);
      PayoutLedgerModel payoutLedgerModel = PayoutLedgerModel.builder()
        .amount(closerTransfer.getAmount())
        .invoice(invoiceModel)
        .userWorkspace(invoiceModel.getUserWorkspace())
        .status(PayoutStatusType.PAYMENT_TRANSFERED)
        .invoice(invoiceModel)
        .build();
      repository.save(payoutLedgerModel);

      if (customerModel.getSetterInvolved()) {
        TransferCreateParams setterTransferParams = TransferCreateParams.builder()
          .setAmount(Double.valueOf(invoiceModel.getSnapshotCommSetterNet()).longValue())
          .setCurrency(invoiceModel.getCurrency())
          .setDestination(customerModel.getCreator().getStripeId())
          .build();
        Transfer setterTransfer = Transfer.create(setterTransferParams);
        PayoutLedgerModel payoutLedgerSetterModel = PayoutLedgerModel.builder()
          .amount(setterTransfer.getAmount())
          .invoice(invoiceModel)
          .userWorkspace(invoiceModel.getUserWorkspace())
          .status(PayoutStatusType.PAYMENT_TRANSFERED)
          .invoice(invoiceModel)
          .build();
        repository.save(payoutLedgerSetterModel);
      }
      return JapiResponse.success(null);
    } catch (Exception e) { throw e; }
  }
}
