package com.paydai.api.application;

import com.paydai.api.domain.annotation.TryCatchException;
import com.paydai.api.domain.model.*;
import com.paydai.api.domain.repository.AccountLedgerRepository;
import com.paydai.api.domain.repository.InvoiceRepository;
import com.paydai.api.domain.repository.PayoutLedgerRepository;
import com.paydai.api.domain.service.AccountLedgerService;
import com.paydai.api.domain.service.PayoutLedgerService;
import com.paydai.api.presentation.dto.payout.PayoutDtoMapper;
import com.paydai.api.presentation.dto.payout.PayoutRecord;
import com.paydai.api.presentation.dto.payout.PayoutTnxOverviewDto;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.Balance;
import com.stripe.model.Transfer;
import com.stripe.net.RequestOptions;
import com.stripe.param.TransferCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutLedgerServiceImpl implements PayoutLedgerService {
  private final PayoutLedgerRepository repository;
  private final PayoutDtoMapper payoutDtoMapper;
  private final InvoiceRepository invoiceRepository;
  private final AccountLedgerService accountLedgerService;

  @Override
  @TryCatchException
  public JapiResponse transferToSalesRep(String stripeInvoiceCode) throws StripeException {
    PayoutLedgerModel payoutLedgerModel = repository.findPayoutByStripeInvoiceId(stripeInvoiceCode);

    if (payoutLedgerModel != null) {
      if (payoutLedgerModel.getStatus().equals(PayoutStatusType.PAYMENT_TRANSFERED)) {
        return JapiResponse.success(null);
      }
    }
    InvoiceModel invoiceModel = invoiceRepository.findByStripeInvoiceCode(stripeInvoiceCode);

    CustomerModel customerModel = invoiceModel.getCustomer();

    UserModel closer = customerModel.getCloser();

    WorkspaceModel workspaceModel = invoiceModel.getUserWorkspace().getWorkspace();

    // TRANSFER TO CLOSER
    TransferCreateParams closerTransferParams = TransferCreateParams.builder()
      .setAmount(Double.valueOf(invoiceModel.getSnapshotCommCloserNet()).longValue() * 100) //todo multiply by sm_unit
      .setCurrency(invoiceModel.getCurrency())
      .setDestination(customerModel.getCloser().getStripeId())
      .build();

    Transfer closerTransfer = Transfer.create(closerTransferParams);

    // SAVE TNX ON PAYDAI
    PayoutLedgerModel closerLedger = PayoutLedgerModel.builder()
      .credit((double) closerTransfer.getAmount() / 100)
      .revenue(invoiceModel.getSnapshotCommCloser())
      .fee(invoiceModel.getSnapshotCommCloser() - invoiceModel.getSnapshotCommCloserNet())
      .currency(closerTransfer.getCurrency())
      .stripeInvoiceCode(stripeInvoiceCode)
      .invoice(invoiceModel)
      .workspace(workspaceModel)
      .user(closer)
      .userWorkspace(invoiceModel.getUserWorkspace())
      .status(PayoutStatusType.PAYMENT_TRANSFERED)
      .invoice(invoiceModel)
      .build();

    repository.save(closerLedger);

    // UPDATE BALANCE FOR CLOSER BY STRIPE BALANCE
    accountLedgerService.updateSalesRepAccountLedgerBalance(closer.getId());

    // PROCESS SETTER TRANSFER IF INVOLVED
    if (customerModel.getSetterInvolved()) {
      UserModel setter = customerModel.getSetter();

      TransferCreateParams setterTransferParams = TransferCreateParams.builder()
        .setAmount(Double.valueOf(invoiceModel.getSnapshotCommSetterNet()).longValue() * 100) // todo: do not hardcode
        .setCurrency(invoiceModel.getCurrency())
        .setDestination(setter.getStripeId())
        .build();
      Transfer setterTransfer = Transfer.create(setterTransferParams);

      repository.save(
        PayoutLedgerModel.builder()
          .revenue(invoiceModel.getSnapshotCommSetter())
          .credit((double) setterTransfer.getAmount() / 100)
          .fee(invoiceModel.getSnapshotCommSetter() - invoiceModel.getSnapshotCommSetterNet())
          .invoice(invoiceModel)
          .currency(setterTransfer.getCurrency())
          .stripeInvoiceCode(stripeInvoiceCode)
          .workspace(workspaceModel)
          .user(setter)
          .userWorkspace(invoiceModel.getUserWorkspace())
          .status(PayoutStatusType.PAYMENT_TRANSFERED)
          .invoice(invoiceModel)
          .build()
      );

      // UPDATE SETTER BALANCE BY STRIPE BALANCE
      accountLedgerService.updateSalesRepAccountLedgerBalance(setter.getId());
    }
    return JapiResponse.success(null);
  }

  @Override
  @TryCatchException
  public JapiResponse getPayoutLedgerTransactions(UUID userId, UUID workspaceId) {
    List<PayoutLedgerModel> payoutLedgerModels = repository.findPayoutTransactions(userId, workspaceId);

    if (payoutLedgerModels.isEmpty()) {
      return JapiResponse.success(payoutLedgerModels);
    }
    List<PayoutRecord> payoutRecord = payoutLedgerModels.stream().map(payoutDtoMapper).toList();
    return JapiResponse.success(payoutRecord);
  }

  @Override
  @TryCatchException
  public JapiResponse getPayoutLedgerTransactions(UUID userId) {
    List<PayoutLedgerModel> payoutLedgerModels = repository.findPayoutTransactions(userId);
    if (payoutLedgerModels.isEmpty()) {
      return JapiResponse.success(payoutLedgerModels);
    }
    List<PayoutRecord> payoutRecord = payoutLedgerModels.stream().map(payoutDtoMapper).toList();
    return JapiResponse.success(payoutRecord);
  }

  @Override
  @TryCatchException
  public JapiResponse getTransactionOverview(UUID userId, UUID workspaceId) {
    List<PayoutLedgerModel> payoutLedgerModels = repository.findPayoutTransactions(userId, workspaceId);
    PayoutTnxOverviewDto overviewDto = sumTotalPayout(payoutLedgerModels);
    return JapiResponse.success(overviewDto);
  }

  @Override
  @TryCatchException
  public JapiResponse getTransactionOverview(UUID userId) {
    List<PayoutLedgerModel> payoutLedgerModels = repository.findPayoutTransactions(userId);
    PayoutTnxOverviewDto overviewDto = sumTotalPayout(payoutLedgerModels);
    return JapiResponse.success(overviewDto);
  }

  private PayoutTnxOverviewDto sumTotalPayout(@NotNull List<PayoutLedgerModel> payoutLedgerModels) {
    return payoutLedgerModels.stream().reduce(
      new PayoutTnxOverviewDto(0, 0, 0), // identity
      (dto, payout) -> {
        dto.setTotalRevenues(dto.getTotalRevenues() + payout.getRevenue());
        dto.setTotalCredits(dto.getTotalCredits() + payout.getCredit());
        dto.setTotalFees(dto.getTotalFees() + payout.getFee());
        return dto;
      }, // accumulator
      (dto1, dto2) -> {
        dto1.setTotalRevenues(dto1.getTotalRevenues() + dto2.getTotalRevenues());
        dto1.setTotalCredits(dto1.getTotalCredits() + dto2.getTotalCredits());
        dto1.setTotalFees(dto1.getTotalFees() + dto2.getTotalFees());
        return dto1;
      } // combiner
    );
  }
}
