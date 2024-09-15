package com.paydai.api.application;

import com.paydai.api.domain.annotation.TryCatchException;
import com.paydai.api.domain.exception.NotFoundException;
import com.paydai.api.domain.model.*;
import com.paydai.api.domain.repository.InvoiceRepository;
import com.paydai.api.domain.repository.TransactionRepository;
import com.paydai.api.domain.repository.RefundRepository;
import com.paydai.api.domain.repository.UserWorkspaceRepository;
import com.paydai.api.domain.service.TransactionService;
import com.paydai.api.infrastructure.config.AppConfig;
import com.paydai.api.presentation.dto.transaction.TransactionDtoMapper;
import com.paydai.api.presentation.dto.transaction.TransactionRecord;
import com.paydai.api.presentation.dto.transaction.TransactionOverviewDto;
import com.paydai.api.presentation.request.TransferRequest;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Invoice;
import com.stripe.model.Transfer;
import com.stripe.net.RequestOptions;
import com.stripe.param.ChargeCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
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
  private final AppConfig appConfig;
  private final TransactionRepository repository;
  private final RefundRepository refundRepository;
  private final InvoiceRepository invoiceRepository;
  private final TransactionDtoMapper transactionDtoMapper;
  private final UserWorkspaceRepository userWorkspaceRepository;

  @Override
  @TryCatchException
  @Transactional
  public JapiResponse transferToSalesRep(Invoice invoice) throws StripeException {
    TransactionModel transactionModel = repository.findTransactionByStripeInvoiceId(invoice.getId());

    if (transactionModel != null && transactionModel.getStatus().equals(TxnStatusType.PAYMENT_TRANSFERRED)) return JapiResponse.success(null);

    InvoiceModel invoiceModel = invoiceRepository.findByStripeInvoiceCode(invoice.getId());

    String stripeInvoiceId = invoiceModel.getStripeInvoiceId();
    String currency = invoiceModel.getCurrency();
    double invoiceAmt = invoiceModel.getAmount();
    double appFee = invoiceModel.getApplicationFee();
    CustomerModel customerModel = invoiceModel.getCustomer();

    double merchantAmt = invoiceAmt - appFee;
    WorkspaceModel workspaceModel = invoiceModel.getUserWorkspace().getWorkspace();
    UserModel merchant = workspaceModel.getOwner();
    persistTransactionToDatabase(invoiceAmt, merchantAmt, appFee, invoiceModel, stripeInvoiceId, currency, workspaceModel, merchant, TxnType.INVOICE_SETTLEMENT, TxnEntryType.CREDIT, TxnStatusType.PAYMENT_TRANSFERRED, customerModel.getName(), workspaceModel.getName(), "CUSTOMER:invoice-settlement-fund");

    String giver = "Paydai";
    String remark = "PAYDAI:payout-settlement-fund";

    // TRANSFER TO CLOSER
    UserModel closer = customerModel.getCloser();
    double closerNetComm = invoiceModel.getSnapshotCommCloserNet();
    double closerNet = handleLiability(closer, workspaceModel, closerNetComm, currency, invoiceModel/*, invoice*/);
    if (closerNet > 0) performTransfer(closerNet, currency, closer.getStripeId()/*, true, invoice.getPaymentIntent()*/);
    double clRevenue = invoiceModel.getSnapshotCommCloser();
    double clFee = clRevenue - invoiceModel.getSnapshotCommCloserNet();
    String closerName = closer.getFirstName() + " " + closer.getLastName();
    persistTransactionToDatabase(clRevenue, closerNetComm, clFee, invoiceModel, stripeInvoiceId, currency, workspaceModel, closer, TxnType.PAYOUT, TxnEntryType.CREDIT, TxnStatusType.PAYMENT_TRANSFERRED, giver, closerName, remark);

    // PROCESS SETTER TRANSFER IF INVOLVED
    if (customerModel.getSetterInvolved()) {
      UserModel setter = customerModel.getSetter();
      double stRevenue = invoiceModel.getSnapshotCommSetter();
      double setterNetComm = invoiceModel.getSnapshotCommSetterNet();
      double stFee = stRevenue - setterNetComm;
      double setterNet = handleLiability(setter, workspaceModel, setterNetComm, currency, invoiceModel/*, invoice*/);
      String setterName = setter.getFirstName() + " " + setter.getLastName();
      persistTransactionToDatabase(stRevenue, setterNetComm, stFee, invoiceModel, stripeInvoiceId, currency, workspaceModel, setter, TxnType.PAYOUT, TxnEntryType.CREDIT, TxnStatusType.PAYMENT_TRANSFERRED, giver, setterName, remark);
      if (setterNet > 0) performTransfer(setterNet, currency, setter.getStripeId()/*, true, invoice.getCharge()*/);
    }

    List<InvoiceManagerModel> involvedManagers = invoiceModel.getInvolvedManagers();
    if (!involvedManagers.isEmpty()) {
      for (InvoiceManagerModel invoiceManagerModel : involvedManagers) {
        UserModel manager = invoiceManagerModel.getManager();

        double managerComm = invoiceManagerModel.getSnapshotCommManagerNet();

        double managerNet = handleLiability(invoiceManagerModel.getManager(), workspaceModel, managerComm, currency, invoiceModel/*, invoice*/);

        if (managerNet > 0) {
          currency = invoiceManagerModel.getInvoice().getCurrency();

          String managerStripeId = invoiceManagerModel.getManager().getStripeId();

          Transfer managerTransfer = performTransfer(managerNet, currency, managerStripeId/*, true, invoice.getPaymentIntent()*/);

          repository.save(
            TransactionModel.builder()
              .amount((double) managerTransfer.getAmount() / 100)
              .entryType(TxnEntryType.CREDIT)
              .revenue(invoiceManagerModel.getSnapshotCommManager())
              .fee(0.0)
              .giver("Paydai")
              .receiver(manager.getFirstName() + " " + manager.getLastName())
              .currency(managerTransfer.getCurrency())
              .stripeInvoiceCode(invoice.getId())
              .invoice(invoiceModel)
              .remark("PAYDAI:transaction-settlement-fund")
              .workspace(workspaceModel)
              .txnType(TxnType.PAYOUT)
              .user(invoiceManagerModel.getManager())
              .status(TxnStatusType.PAYMENT_TRANSFERRED)
              .invoice(invoiceModel)
              .build()
          );
        }
      }
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
      .setAmount(Double.valueOf(payload.getAmount()).longValue() * 100) //todo: use amount-dto
      .setCurrency(payload.getCurrency())
      .setDestination(appConfig.getStripeAcct())
      .build();
    Transfer salesRepTransfer = Transfer.create(salesTransferParams, requestOptions);


    Transfer.create(
      TransferCreateParams.builder()
        .setAmount(Double.valueOf(payload.getAmount()).longValue() * 100) //todo: use amount-dto
        .setCurrency(payload.getCurrency())
        .setDestination(salesRep.getUser().getStripeId())
        .build()
    );

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
      .status(TxnStatusType.PAYMENT_TRANSFERRED)
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
        .status(TxnStatusType.PAYMENT_TRANSFERRED)
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

  // *************** PRIVATE METHODS ******************* //
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
  private double handleLiability(UserModel salesRep, WorkspaceModel workspace, double commission, String currency, InvoiceModel invoiceModel/*, Invoice invoice*/) {
    String stripeInCode = invoiceModel.getStripeInvoiceId();
    String giver = salesRep.getFirstName() + " " + salesRep.getLastName();
    String remark = "REFUND:invoice-refund-settlement";
    List<RefundModel> salesRepLiabilities = refundRepository.findSalesRepLiabilities(salesRep.getId(), workspace.getId());

    UserModel merchant = workspace.getOwner();

    if (salesRepLiabilities != null && !salesRepLiabilities.isEmpty()) {

      for (RefundModel salesRepLiability : salesRepLiabilities) {

        double liabilityAmount = salesRepLiability.getAmount() - salesRepLiability.getTotalPaid();

        // Ensure commission stays above 1 after liability deduction
        if (commission < liabilityAmount) {
          performTransfer(commission, currency, merchant.getStripeId()/*, true, invoice.getCharge()*/);
          salesRepLiability.setTotalPaid(commission);
          salesRepLiability.setStatus(RefundStatus.PARTIALLY_PAID);
          persistTransactionToDatabase(commission, commission, 0.0, invoiceModel, stripeInCode, currency, workspace, merchant, TxnType.REFUND, TxnEntryType.CREDIT, TxnStatusType.PAYMENT_TRANSFERRED, giver, workspace.getName(), remark);
          persistTransactionToDatabase(commission, commission, 0.0, invoiceModel, stripeInCode, currency, workspace, salesRep, TxnType.REFUND, TxnEntryType.DEBIT, TxnStatusType.PAYMENT_TRANSFERRED, giver, workspace.getName(), remark);
          commission = Math.max(0, commission - liabilityAmount);
          refundRepository.save(salesRepLiability);
          break;
        } else {
          performTransfer(liabilityAmount, currency, merchant.getStripeId()/*, true, invoice.getCharge()*/);
          salesRepLiability.setStatus(RefundStatus.PAID);
          salesRepLiability.setTotalPaid(liabilityAmount);
          persistTransactionToDatabase(liabilityAmount, liabilityAmount, 0.0, invoiceModel, stripeInCode, currency, workspace, merchant, TxnType.REFUND, TxnEntryType.CREDIT, TxnStatusType.PAYMENT_TRANSFERRED, giver, workspace.getName(), remark);
          persistTransactionToDatabase(liabilityAmount, liabilityAmount, 0.0, invoiceModel, stripeInCode, currency, workspace, salesRep, TxnType.REFUND, TxnEntryType.DEBIT, TxnStatusType.PAYMENT_TRANSFERRED, giver, workspace.getName(), remark);
          commission = commission - liabilityAmount;
          refundRepository.save(salesRepLiability);
        }
      }
    }
    return commission;
  }

  private Transfer performTransfer(double amount, String currency, String stripeId /*boolean useSource, String chargeId*/) {
    try {
      TransferCreateParams.Builder paramsBuilder = TransferCreateParams.builder()
        .setAmount(Double.valueOf(amount).longValue() * 100) //todo use amount dto
        .setCurrency(currency)
        .setDestination(stripeId);

//      if (Boolean.TRUE.equals(useSource)) {
//        paramsBuilder.setSourceTransaction(chargeId);
//      }

      TransferCreateParams params = paramsBuilder.build();

      return Transfer.create(params);
    } catch (StripeException e) {
      throw new com.paydai.api.domain.exception.StripeException(e.getMessage());
    }
  }

  private TransactionModel persistTransactionToDatabase(double revenue, double amount, double fee, InvoiceModel invoiceModel, String stripeInvoiceCode, String currency, WorkspaceModel workspaceModel, UserModel user, TxnType txnType, TxnEntryType entryType, TxnStatusType txnStatusType, String giver, String receiver, String remark) {
    return  repository.save(
      TransactionModel.builder()
        .revenue(revenue)
        .amount(amount)
        .entryType(TxnEntryType.CREDIT)
        .fee(fee)
        .remark(remark)
        .giver(giver)
        .receiver(receiver)
        .currency(currency)
        .stripeInvoiceCode(stripeInvoiceCode)
        .workspace(workspaceModel)
        .user(user)
        .txnType(txnType)
        .entryType(entryType)
        .status(txnStatusType)
        .invoice(invoiceModel)
        .build()
    );
  }
}
