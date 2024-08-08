package com.paydai.api.application;

import com.paydai.api.domain.exception.NotFoundException;
import com.paydai.api.domain.model.*;
import com.paydai.api.domain.repository.AccountLedgerRepository;
import com.paydai.api.domain.repository.InvoiceRepository;
import com.paydai.api.domain.repository.PayoutLedgerRepository;
import com.paydai.api.domain.service.PayoutLedgerService;
import com.paydai.api.presentation.dto.payout.PayoutDtoMapper;
import com.paydai.api.presentation.dto.payout.PayoutRecord;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.Transfer;
import com.stripe.param.TransferCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutLedgerServiceImpl implements PayoutLedgerService {
  private final PayoutLedgerRepository repository;
  private final PayoutDtoMapper payoutDtoMapper;
  private final InvoiceRepository invoiceRepository;
  private final AccountLedgerRepository accountLedgerRepository;

  @Override
  public JapiResponse transferToSalesRep(String stripeInvoiceCode) throws StripeException {
    try {
      PayoutLedgerModel payoutLedgerModel = repository.findPayoutByStripeInvoiceId(stripeInvoiceCode);

      if (payoutLedgerModel != null) {
        if (payoutLedgerModel.getStatus().equals(PayoutStatusType.PAYMENT_TRANSFERED)) {
          return JapiResponse.success(null);
        }
      }

      InvoiceModel invoiceModel = payoutLedgerModel.getInvoice();

      CustomerModel customerModel = invoiceModel.getCustomer();

      // TRANSFER TO CLOSER
      TransferCreateParams closerTransferParams = TransferCreateParams.builder()
        .setAmount(Double.valueOf(invoiceModel.getSnapshotCommCloserNet()).longValue())
        .setCurrency(invoiceModel.getCurrency())
        .setDestination(customerModel.getCloser().getStripeId())
        .build();

      Transfer closerTransfer = Transfer.create(closerTransferParams);

      // SAVE TNX ON PAYDAI
      repository.save(
        PayoutLedgerModel.builder()
          .amount(closerTransfer.getAmount())
          .invoice(invoiceModel)
          .userWorkspace(invoiceModel.getUserWorkspace())
          .status(PayoutStatusType.PAYMENT_TRANSFERED)
          .invoice(invoiceModel)
          .build()
      );

      // UPDATE BALANCE FOR CLOSER
      accountLedgerRepository.save(
        AccountLedgerModel.builder()
          .user(customerModel.getCloser())
          .revenue(closerTransfer.getAmount())
          .workspace(invoiceModel.getWorkspace())
          .build()
      );

      if (customerModel.getSetterInvolved()) {
        TransferCreateParams setterTransferParams = TransferCreateParams.builder()
          .setAmount(Double.valueOf(invoiceModel.getSnapshotCommSetterNet()).longValue())
          .setCurrency(invoiceModel.getCurrency())
          .setDestination(customerModel.getCreator().getStripeId())
          .build();
        Transfer setterTransfer = Transfer.create(setterTransferParams);

        repository.save(
          PayoutLedgerModel.builder()
            .amount(setterTransfer.getAmount())
            .invoice(invoiceModel)
            .userWorkspace(invoiceModel.getUserWorkspace())
            .status(PayoutStatusType.PAYMENT_TRANSFERED)
            .invoice(invoiceModel)
            .build()
        );
      }
      return JapiResponse.success(null);
    } catch (Exception e) { throw e; }
  }

  @Override
  public JapiResponse getPayoutLedgerTransactions(UUID userId, UUID workspaceId) {
    try {
      List<PayoutLedgerModel> payoutLedgerModels = repository.findPayoutTransactions(userId, workspaceId);

      if (payoutLedgerModels.isEmpty()) {
        return JapiResponse.success(payoutLedgerModels);
      }
      List<PayoutRecord> payoutRecord = payoutLedgerModels.stream().map(payoutDtoMapper).toList();
      return JapiResponse.success(payoutRecord);
    } catch (Exception e) { throw e;}
  }

  @Override
  public JapiResponse getPayoutLedgerTransactions(UUID userId) {
    try {
      List<PayoutLedgerModel> payoutLedgerModels = repository.findPayoutTransactions(userId);
      if (payoutLedgerModels.isEmpty()) {
        return JapiResponse.success(payoutLedgerModels);
      }
      List<PayoutRecord> payoutRecord = payoutLedgerModels.stream().map(payoutDtoMapper).toList();
      return JapiResponse.success(payoutRecord);
    } catch (Exception e) { throw e;}
  }
}
