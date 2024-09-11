package com.paydai.api.application;

import com.paydai.api.domain.annotation.TryCatchException;
import com.paydai.api.domain.exception.NotFoundException;
import com.paydai.api.domain.model.*;
import com.paydai.api.domain.repository.*;
import com.paydai.api.domain.service.InvoiceHelperService;
import com.paydai.api.domain.service.InvoiceService;
import com.paydai.api.domain.service.ProfileService;
import com.paydai.api.presentation.dto.AmountDto;
import com.paydai.api.presentation.dto.commission.CommissionRecord;
import com.paydai.api.presentation.dto.invoice.*;
import com.paydai.api.presentation.request.CalcRequest;
import com.paydai.api.presentation.request.InvoiceRequest;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.net.RequestOptions;
import com.stripe.param.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
  private final InvoiceRepository repository;
  private final ProfileService profileService;
  private final InvoiceDtoMapper invoiceDtoMapper;
  private final CustomerRepository customerRepository;
  private final CalculatorServiceImpl calculatorService;
  private final InvoiceHelperService invoiceHelperService;
  private final UserWorkspaceRepository userWorkspaceRepository;

  @Override
  @TryCatchException
  @Transactional
  public JapiResponse create(InvoiceRequest payload) throws StripeException {
    UserModel userModel = profileService.getLoggedInUser();

    UserWorkspaceModel closerWorkspaceModel = userWorkspaceRepository.findOneByUserId(userModel.getId(), payload.getWorkspaceId());

    if (closerWorkspaceModel == null) throw new NotFoundException("Invalid closer id");

    CommissionSettingModel commissionSettingModel = closerWorkspaceModel.getCommission();

    String connectedAccountId = closerWorkspaceModel.getWorkspace().getOwner().getStripeId();

    AmountDto amountDto = AmountDto.getAmountDto(payload.getUnitPrice(), payload.getCurrency());

    Product product = invoiceHelperService.createProductInStripe(payload, connectedAccountId);

    Price price = invoiceHelperService.createPriceInStripe(payload, product, connectedAccountId);

    CustomerModel customerModel = customerRepository.findByCustomerId(payload.getCustomerId());

    CommSplitScenarioType scenarioType = CommSplitScenarioType.CLOSER_ONLY;
    List<TeamModel> closerManagers = invoiceHelperService.getTeamMembers(closerWorkspaceModel);
    CommissionData commissionData = invoiceHelperService.getSetterCommissionData(customerModel, payload, scenarioType);

    // customer from stripe before below
    Customer customer = invoiceHelperService.createCustomerInStripe(customerModel, connectedAccountId);

    // CALCULATE COMMISSIONS
    CalcRequest calcRequest = invoiceHelperService.buildCalcRequest(payload, commissionSettingModel, commissionData, scenarioType, closerManagers, commissionData.getManagerTeams());

    CommissionRecord commissionRecord = calculatorService.displayCommissions(calcRequest);

    Invoice invoice = invoiceHelperService.createStripeInvoice(payload, customer, commissionRecord, connectedAccountId);

    InvoiceItem invoiceItem = invoiceHelperService.createStripeInvoiceItem(invoice, price, customer, payload, connectedAccountId);

    InvoiceModel invoiceModel = invoiceHelperService.saveInvoiceToDatabase(payload, amountDto, commissionRecord, closerWorkspaceModel, customerModel, commissionSettingModel, product, invoice, invoiceItem);

    return JapiResponse.success(getInvoiceRecordDetails(invoiceModel));
  }

  @Override
  public JapiResponse getInvoiceToCustomer(UUID customerId) {
    return JapiResponse.success(null);
  }

  @Override
  @TryCatchException
  public JapiResponse getWorkspaceInvoicesToCustomers(UUID workspaceId) {
    List<InvoiceModel> invoiceModels = repository.findByWorkspaceInvoices(workspaceId);
    List<InvoiceRecord> invoiceDtos = new ArrayList<>();
    if (!invoiceModels.isEmpty()) {
      invoiceDtos = invoiceModels.stream().map(this::getInvoiceRecordDetails).toList();
    }
    return JapiResponse.success(invoiceDtos);
  }

  @Override
  @TryCatchException
  public JapiResponse getInvoice(String invoiceCode) {
    InvoiceModel invoiceModel = validateInvoice(invoiceCode);
    return JapiResponse.success(getInvoiceRecordDetails(invoiceModel));
  }

  @Override
  @TryCatchException
  public JapiResponse finalizeInvoice(String invoiceCode) throws StripeException {
    InvoiceModel invoiceModel = validateInvoice(invoiceCode);

    String connectedAccountId = invoiceModel.getUserWorkspace().getWorkspace().getOwner().getStripeId();

    RequestOptions requestOptions = RequestOptions.builder().setStripeAccount(connectedAccountId).build();

    Invoice invoice = Invoice.retrieve(invoiceModel.getStripeInvoiceId(), requestOptions);

    try {
    InvoiceFinalizeInvoiceParams invoiceFinalizeInvoiceParams = InvoiceFinalizeInvoiceParams.builder().build();

    invoice = invoice.finalizeInvoice(invoiceFinalizeInvoiceParams, requestOptions);
    } catch (StripeException e) {
      throw new com.paydai.api.domain.exception.StripeException(e.getMessage());
    }

    Map<String, Object> response = invoiceDetails(invoice, invoiceModel);

    repository.updateInvoiceByInvoiceCode(invoiceCode, response.toString(), invoice.getHostedInvoiceUrl(), invoice.getInvoicePdf(), invoice.getStatus(), String.valueOf(InvoiceStatus.FINALIZED));

    return JapiResponse.success(response);
  }

  @Override
  @TryCatchException
  public JapiResponse sendInvoice(String invoiceCode) throws StripeException {
    InvoiceModel invoiceModel = validateInvoice(invoiceCode);

    String connectedAccountId = invoiceModel.getUserWorkspace().getWorkspace().getOwner().getStripeId();

    RequestOptions requestOptions = RequestOptions.builder().setStripeAccount(connectedAccountId).build();

    Invoice invoice = Invoice.retrieve(invoiceModel.getStripeInvoiceId(), requestOptions);
    try {
    InvoiceSendInvoiceParams invoiceSendInvoiceParams = InvoiceSendInvoiceParams.builder().build();

    invoice = invoice.sendInvoice(invoiceSendInvoiceParams, requestOptions);

    } catch (StripeException e) {
      throw new com.paydai.api.domain.exception.StripeException(e.getMessage());
    }

    repository.updateInvoiceStatus(invoiceCode, invoice.getStatus(), InvoiceStatus.SENT.toString());

    return JapiResponse.success(invoiceDetails(invoice, invoiceModel));
  }

  @Override
  @TryCatchException
  public JapiResponse filterInvoice(UUID workspaceId, InvoiceStatus status) {
    List<InvoiceModel> invoiceModels = repository.findByFilterWorkspaceInvoices(workspaceId, status.name());
    List<InvoiceRecord> invoiceDtos = new ArrayList<>();
    if (!invoiceModels.isEmpty()) {
      invoiceDtos = invoiceModels.stream().map(this::getInvoiceRecordDetails).toList();
    }
    return JapiResponse.success(invoiceDtos);
  }

  @Override
  @TryCatchException
  public JapiResponse getSalesRepInvoice(String invoiceCode) {
    InvoiceModel invoiceModel = validateInvoice(invoiceCode);

    UserModel closer = invoiceModel.getUserWorkspace().getUser();

    UserModel setter = invoiceModel.getCustomer().getSetter();

    List<InvoiceSalesRepDto> salesReps = new ArrayList<>();

    if (!invoiceModel.getInvolvedManagers().isEmpty())
      for (InvoiceManagerModel invoiceManagerModel : invoiceModel.getInvolvedManagers()) {
        UUID srId = invoiceManagerModel.getManager().getId();
        double credit = invoiceManagerModel.getSnapshotCommManagerNet();
        String name = invoiceManagerModel.getManager().getFirstName();
        InvoiceSalesRepDto invoiceSalesRepDto = InvoiceSalesRepDto.getInvoiceSalesRepDto(srId, credit, name, "manager");
        salesReps.add(invoiceSalesRepDto);
      }

    salesReps.add(InvoiceSalesRepDto.getInvoiceSalesRepDto(closer.getId(), invoiceModel.getSnapshotCommCloserNet(), closer.getFirstName(), "closer"));

    if (setter != null)
        salesReps.add(InvoiceSalesRepDto.getInvoiceSalesRepDto(setter.getId(), invoiceModel.getSnapshotCommCloserNet(), setter.getFirstName(), "setter"));

    return JapiResponse.success(salesReps);
  }

  private InvoiceModel validateInvoice(String invoiceCode) {
    InvoiceModel invoiceModel = repository.findByInvoiceCode(invoiceCode);
    if (invoiceModel == null) throw new NotFoundException("Invoice code is invalid");
    return invoiceModel;
  }

  private Map<String, Object> invoiceDetails(Invoice invoice, InvoiceModel invoiceModel) {
    Map<String, Object> response = new HashMap<>();
    response.put("stripeInvoiceId", invoice.getId());
    response.put("stripeInvoicePdf", invoice.getInvoicePdf());
    response.put("stripeInvoiceHostedUrl", invoice.getHostedInvoiceUrl());
    response.put("stripeInvoiceStatus", invoice.getStatus());
    response.put("stripeInvoiceFrom", invoice.getFromInvoice());
    response.put("stripeInvoiceAmountDue", invoice.getAmountDue());
    response.put("stripeInvoiceAmountPaid", invoice.getAmountPaid());
    response.put("stripeInvoiceAmountRemains", invoice.getAmountRemaining());
    response.put("stripeInvoiceApplicationFee", invoice.getApplicationFeeAmount());
    response.put("stripeInvoiceCollectMethod", invoice.getCollectionMethod());
    response.put("stripeInvoiceCustomerEmail", invoice.getCustomerEmail());
    response.put("stripeInvoiceCustomerName", invoice.getCustomerName());
    response.put("stripeInvoiceEndingBal", invoice.getEndingBalance());
    response.put("invoiceCode", invoiceModel.getInvoiceCode());
    response.put("invoiceProductName", invoiceModel.getProduct().getItem());
    return response;
  }

  private InvoiceRecord getInvoiceRecordDetails(InvoiceModel invoiceModel) {
    CalcRequest calcRequest = CalcRequest.builder()
      .scenario(invoiceModel.getCommSplitScenario())
      .revenue(invoiceModel.getAmount())
      .closerPercent(invoiceModel.getSnapshotCommCloserPercent())
      .setterPercent(invoiceModel.getSnapshotCommSetterPercent())
      .build();

    CommissionRecord commissionRecord = calculatorService.displayCommissions(calcRequest);

    InvoiceDto invoiceDto = InvoiceDto.getInvoiceDto(invoiceModel, commissionRecord, invoiceModel.getInvolvedManagers());

    return invoiceDtoMapper.apply(invoiceDto);
  }
}
