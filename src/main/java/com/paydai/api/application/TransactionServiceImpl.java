package com.paydai.api.application;

import com.paydai.api.domain.annotation.TryCatchException;
import com.paydai.api.domain.exception.NotFoundException;
import com.paydai.api.domain.model.*;
import com.paydai.api.domain.repository.InvoiceRepository;
import com.paydai.api.domain.repository.TransactionRepository;
import com.paydai.api.domain.repository.RefundRepository;
import com.paydai.api.domain.repository.UserWorkspaceRepository;
import com.paydai.api.domain.service.AccountLedgerService;
import com.paydai.api.domain.service.TransactionService;
import com.paydai.api.presentation.dto.transaction.TransactionDtoMapper;
import com.paydai.api.presentation.dto.transaction.TransactionRecord;
import com.paydai.api.presentation.dto.transaction.TransactionOverviewDto;
import com.paydai.api.presentation.request.TransferRequest;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.Transfer;
import com.stripe.net.RequestOptions;
import com.stripe.param.TransferCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
  private final TransactionRepository repository;
  private final RefundRepository refundRepository;
  private final InvoiceRepository invoiceRepository;
  private final TransactionDtoMapper transactionDtoMapper;
  private final AccountLedgerService accountLedgerService;
  private final UserWorkspaceRepository userWorkspaceRepository;

  @Override
  @TryCatchException
  @Transactional
  public JapiResponse transferToSalesRep(String stripeInvoiceCode) throws StripeException {
    TransactionModel transactionModel = repository.findTransactionByStripeInvoiceId(stripeInvoiceCode);

    if (transactionModel != null) {
      if (transactionModel.getStatus().equals(TransactionStatusType.PAYMENT_TRANSFERRED)) {
        return JapiResponse.success(null);
      }
    }
    InvoiceModel invoiceModel = invoiceRepository.findByStripeInvoiceCode(stripeInvoiceCode);

    CustomerModel customerModel = invoiceModel.getCustomer();

    UserModel closer = customerModel.getCloser();

    WorkspaceModel workspaceModel = invoiceModel.getUserWorkspace().getWorkspace();

    List<InvoiceManagerModel> involvedManagers = invoiceModel.getInvolvedManagers();

    double closerComm = invoiceModel.getSnapshotCommCloserNet();

    double closerNet = handleLiability(closer, workspaceModel, closerComm);

    repository.save(
      TransactionModel.builder()
        .giver(customerModel.getName())
        .receiver(workspaceModel.getName())
        .invoice(invoiceModel)
        .amount(invoiceModel.getAmount() - invoiceModel.getApplicationFee())
        .fee(invoiceModel.getApplicationFee())
        .currency(invoiceModel.getCurrency())
        .remark("CUSTOMER:invoice-settlement-fund")
        .entryType(TxnEntryType.CREDIT)
        .stripeInvoiceCode(invoiceModel.getStripeInvoiceId())
        .revenue(invoiceModel.getAmount())
        .status(TransactionStatusType.PAYMENT_TRANSFERRED)
        .txnType(TxnType.INVOICE_SETTLEMENT)
        .user(workspaceModel.getOwner())
        .workspace(workspaceModel)
        .build()
    );

    // TRANSFER TO CLOSER
    if (closerComm > 0) {
      TransferCreateParams closerTransferParams = TransferCreateParams.builder()
        .setAmount(Double.valueOf(closerNet).longValue() * 100) //todo multiply by sm_unit
        .setCurrency(invoiceModel.getCurrency())
        .setDestination(customerModel.getCloser().getStripeId())
        .build();
      Transfer closerTransfer = Transfer.create(closerTransferParams);

      // SAVE TNX ON PAYDAI
      TransactionModel closerLedger = TransactionModel.builder()
        .amount((double) closerTransfer.getAmount() / 100)
        .entryType(TxnEntryType.CREDIT)
        .revenue(invoiceModel.getSnapshotCommCloser())
        .fee(invoiceModel.getSnapshotCommCloser() - invoiceModel.getSnapshotCommCloserNet())
        .currency(closerTransfer.getCurrency())
        .stripeInvoiceCode(stripeInvoiceCode)
        .remark("PAYDAI:payout-settlement-fund")
        .giver("Paydai")
        .receiver(closer.getFirstName() + " " + closer.getLastName())
        .invoice(invoiceModel)
        .txnType(TxnType.PAYOUT)
        .workspace(workspaceModel)
        .user(closer)
        .userWorkspace(invoiceModel.getUserWorkspace())
        .status(TransactionStatusType.PAYMENT_TRANSFERRED)
        .invoice(invoiceModel)
        .build();

      repository.save(closerLedger);
    }

    // UPDATE BALANCE FOR CLOSER BY STRIPE BALANCE
    accountLedgerService.updateSalesRepAccountLedgerBalance(closer.getId());

    // PROCESS SETTER TRANSFER IF INVOLVED
    if (customerModel.getSetterInvolved()) {
      UserModel setter = customerModel.getSetter();

      double setterComm = invoiceModel.getSnapshotCommSetterNet();

      double setterNet = handleLiability(setter, workspaceModel, setterComm);

      if (setterNet > 0) {
        TransferCreateParams setterTransferParams = TransferCreateParams.builder()
          .setAmount(Double.valueOf(setterNet).longValue() * 100) // todo: do not hardcode
          .setCurrency(invoiceModel.getCurrency())
          .setDestination(setter.getStripeId())
          .build();
        Transfer setterTransfer = Transfer.create(setterTransferParams);

        repository.save(
          TransactionModel.builder()
            .revenue(invoiceModel.getSnapshotCommSetter())
            .amount((double) setterTransfer.getAmount() / 100)
            .entryType(TxnEntryType.CREDIT)
            .fee(invoiceModel.getSnapshotCommSetter() - invoiceModel.getSnapshotCommSetterNet())
            .invoice(invoiceModel)
            .remark("PAYDAI:payout-settlement-fund")
            .giver("Paydai")
            .receiver(setter.getFirstName() + " " + setter.getLastName())
            .currency(setterTransfer.getCurrency())
            .stripeInvoiceCode(stripeInvoiceCode)
            .workspace(workspaceModel)
            .user(setter)
            .txnType(TxnType.PAYOUT)
            .userWorkspace(invoiceModel.getUserWorkspace())
            .status(TransactionStatusType.PAYMENT_TRANSFERRED)
            .invoice(invoiceModel)
            .build()
        );
      }

      if (!involvedManagers.isEmpty()) {
        for (InvoiceManagerModel invoiceManagerModel : involvedManagers) {
          UserModel manager = invoiceManagerModel.getManager();

          double managerComm = invoiceManagerModel.getSnapshotCommManagerNet();

          double managerNet = handleLiability(invoiceManagerModel.getManager(), workspaceModel, managerComm);

          if (managerNet > 0) {
            TransferCreateParams managerTransferParams = TransferCreateParams.builder()
              .setAmount(Double.valueOf(invoiceManagerModel.getSnapshotCommManager()).longValue() * 100) //todo multiply by sm_unit
              .setCurrency(invoiceManagerModel.getInvoice().getCurrency())
              .setDestination(invoiceManagerModel.getManager().getStripeId())
              .build();

            Transfer managerTransfer;
            try {
              managerTransfer = Transfer.create(managerTransferParams);
            } catch (StripeException e) {
              throw new com.paydai.api.domain.exception.StripeException(e.getMessage());
            }

            repository.save(
              TransactionModel.builder()
                .amount((double) managerTransfer.getAmount() / 100)
                .entryType(TxnEntryType.CREDIT)
                .revenue(invoiceManagerModel.getSnapshotCommManager())
                .fee(0.0)
                .giver("Paydai")
                .receiver(manager.getFirstName() + " " + manager.getLastName())
                .currency(managerTransfer.getCurrency())
                .stripeInvoiceCode(stripeInvoiceCode)
                .invoice(invoiceModel)
                .remark("PAYDAI:transaction-settlement-fund")
                .workspace(workspaceModel)
                .txnType(TxnType.PAYOUT)
                .user(invoiceManagerModel.getManager())
                .userWorkspace(invoiceManagerModel.getUserWorkspace())
                .status(TransactionStatusType.PAYMENT_TRANSFERRED)
                .invoice(invoiceModel)
                .build()
            );
          }
        }
      }

      // UPDATE SETTER BALANCE BY STRIPE BALANCE
      accountLedgerService.updateSalesRepAccountLedgerBalance(setter.getId());
    }
    return JapiResponse.success(null);
  }

  @Override
  @TryCatchException
  public JapiResponse directTransferToSalesRep(TransferRequest payload) throws StripeException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserModel merchant = (UserModel) authentication.getPrincipal();

    UserWorkspaceModel salesRep = userWorkspaceRepository.findOneByUserId(payload.getSalesRepId(), payload.getWorkspaceId());

    if (salesRep == null) throw new NotFoundException("Invalid sales rep Id");

    UserModel receiver = salesRep.getUser();

    RequestOptions requestOptions = RequestOptions.builder().setStripeAccount(merchant.getStripeId()).build();

    TransferCreateParams salesTransferParams = TransferCreateParams.builder()
      .setAmount(Double.valueOf(payload.getAmount()).longValue() * 100) //todo multiply by sm_unit
      .setCurrency(payload.getCurrency())
      .setDestination(salesRep.getUser().getStripeId())
      .build();
    Transfer salesRepTransfer = Transfer.create(salesTransferParams, requestOptions);

    // CREDIT ENTRY
    TransactionModel salesRepLedger = TransactionModel.builder()
      .entryType(TxnEntryType.CREDIT)
      .amount((double) salesRepTransfer.getAmount() / 100)
      .revenue((double) salesRepTransfer.getAmount() / 100)
      .fee(0.0)
      .remark("DIRECT_TRANSFER:payout-settlement-fund")
      .giver(merchant.getFirstName() + " " + merchant.getLastName())
      .receiver(receiver.getFirstName() + " " + receiver.getLastName())
      .currency(payload.getCurrency())
      .txnType(TxnType.DIRECT_TRANSFER)
      .workspace(salesRep.getWorkspace())
      .user(salesRep.getUser())
      .userWorkspace(salesRep)
      .status(TransactionStatusType.PAYMENT_TRANSFERRED)
      .build();

    repository.save(salesRepLedger);

    // DEBIT ENTRY
    repository.save(
      TransactionModel.builder()
        .entryType(TxnEntryType.DEBIT)
        .amount((double) salesRepTransfer.getAmount() / 100)
        .revenue((double) salesRepTransfer.getAmount() / 100)
        .fee(0.0)
        .remark("DIRECT_TRANSFER:payout-settlement-fund")
        .giver(merchant.getFirstName() + " " + merchant.getLastName())
        .receiver(receiver.getFirstName() + " " + receiver.getLastName())
        .currency(payload.getCurrency())
        .txnType(TxnType.DIRECT_TRANSFER)
        .workspace(salesRep.getWorkspace())
        .user(merchant)
        .status(TransactionStatusType.PAYMENT_TRANSFERRED)
        .build()
    );

    return JapiResponse.success(transactionDtoMapper.apply(salesRepLedger));
  }

  @Override
  @TryCatchException
  public JapiResponse getTransactions(UUID userId, UUID workspaceId) {
    List<TransactionModel> transactionModels = repository.findTransactions(userId, workspaceId);
    if (transactionModels.isEmpty()) {
      return JapiResponse.success(transactionModels);
    }
    List<TransactionRecord> transactionRecord = transactionModels.stream().map(transactionDtoMapper).toList();
    return JapiResponse.success(transactionRecord);
  }

  @Override
  @TryCatchException
  public JapiResponse getTransactionsMerchantUse(UUID workspaceId) {
    List<TransactionModel> transactionModels = repository.findTransactionsMerchant(workspaceId);
    if (transactionModels.isEmpty()) {
      return JapiResponse.success(transactionModels);
    }
    List<TransactionRecord> transactionRecord = transactionModels.stream().map(transactionDtoMapper).toList();
    return JapiResponse.success(transactionRecord);
  }

  @Override
  @TryCatchException
  public JapiResponse getTransactions(UUID userId) {
    List<TransactionModel> transactionModels = repository.findTransactions(userId);
    if (transactionModels.isEmpty()) {
      return JapiResponse.success(transactionModels);
    }
    List<TransactionRecord> transactionRecord = transactionModels.stream().map(transactionDtoMapper).toList();
    return JapiResponse.success(transactionRecord);
  }

  @Override
  @TryCatchException
  public JapiResponse getTransactionOverview(UUID userId, UUID workspaceId) {
    List<TransactionModel> transactionModels = repository.findTransactions(userId, workspaceId);
    TransactionOverviewDto overviewDto = sumTotalTransactions(transactionModels);
    return JapiResponse.success(overviewDto);
  }

  @Override
  @TryCatchException
  public JapiResponse getTransactionOverview(UUID userId) {
    List<TransactionModel> transactionModels = repository.findTransactions(userId);
    TransactionOverviewDto overviewDto = sumTotalTransactions(transactionModels);
    return JapiResponse.success(overviewDto);
  }

  private TransactionOverviewDto sumTotalTransactions(@NotNull List<TransactionModel> transactionModels) {
    return transactionModels.stream().reduce(
      new TransactionOverviewDto(0, 0, 0), // identity
      (dto, payout) -> {
        dto.setTotalRevenues(dto.getTotalRevenues() + payout.getRevenue());
        dto.setTotalCredits(dto.getTotalCredits() + payout.getAmount());
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

  @TryCatchException
  private double handleLiability(UserModel salesRep, WorkspaceModel workspace, double comm) {
    log.info("======== >>>>> " + salesRep.getFirstName());
    List<RefundModel> salesRepLiabilities = refundRepository.findSalesRepLiabilities(salesRep.getId(), workspace.getId());

    if (salesRepLiabilities != null && !salesRepLiabilities.isEmpty()) {
      double totalLiability = 0.0;

      for (RefundModel salesRepLiability : salesRepLiabilities) {
        double liabilityAmount = salesRepLiability.getAmount();

        // Ensure commission stays above 1 after liability deduction
        if (comm - liabilityAmount >= 1) {
          log.info("Sales-Rep-Liability-amount ===> " + liabilityAmount +
                  " Current-total-liability ===> " + totalLiability +
                  " Current-commission ===> " + comm);

          totalLiability += liabilityAmount;
          salesRepLiability.setStatus(RefundStatus.PAID);
          refundRepository.save(salesRepLiability);

          comm -= liabilityAmount;  // Deduct the full liability
        } else {
          // Deduct only enough to leave comm at 1
          log.info("Partial deduction needed. Remaining commission before deduction: " + comm);
          double remainingDeductible = comm - 1;
          totalLiability += remainingDeductible;

          salesRepLiability.setAmount(liabilityAmount - remainingDeductible);
          salesRepLiability.setStatus(RefundStatus.PENDING);
          refundRepository.save(salesRepLiability);

          comm = 1;
          break;
        }
      }

      log.info("Remaining commission after liabilities ===> " + comm);
    }

    log.info("Returned commission ===> " + comm);
    return comm;
  }
}
