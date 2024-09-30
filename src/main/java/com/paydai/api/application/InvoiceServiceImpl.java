package com.paydai.api.application;

import com.paydai.api.domain.annotation.TryCatchException;
import com.paydai.api.domain.exception.ApiRequestException;
import com.paydai.api.domain.exception.ForbiddenException;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
  private final InvoiceRepository repository;
  private final ProfileService profileService;
  private final TeamRepository teamRepository;
  private final RefundRepository refundRepository;
  private final InvoiceDtoMapper invoiceDtoMapper;
  private final ProductRepository productRepository;
  private final CustomerRepository customerRepository;
  private final CalculatorServiceImpl calculatorService;
  private final InvoiceHelperService invoiceHelperService;
  private final UserWorkspaceRepository userWorkspaceRepository;
  private final InvoiceManagerRepository invoiceManagerRepository;

  @Override
  @TryCatchException
  public JapiResponse create(InvoiceRequest payload) throws StripeException {
    UserModel userModel = profileService.getLoggedInUser();

    UserWorkspaceModel closerWorkspaceModel = userWorkspaceRepository.findOneByUserId(userModel.getId(), payload.getWorkspaceId());

    CommissionSettingModel commissionSettingModel = closerWorkspaceModel.getCommission();

    String connectedAccountId = closerWorkspaceModel.getWorkspace().getOwner().getStripeId();

    AmountDto amountDto = AmountDto.getAmountDto(payload.getUnitPrice(), payload.getCurrency());

    RequestOptions requestOptions = RequestOptions.builder().setStripeAccount(connectedAccountId).build();

    ProductCreateParams productCreateParams = ProductCreateParams.builder()
      .setName(payload.getProductName())
      .setDescription(payload.getProductDescription())
      .build();

    Product product = Product.create(productCreateParams, requestOptions);

    PriceCreateParams priceCreateParams = PriceCreateParams.builder()
      .setProduct(product.getId())
      .setUnitAmount(Double.valueOf(amountDto.getLgUnitAmt()).longValue())
      .setCurrency(payload.getCurrency())
      .build();

    Price price = Price.create(priceCreateParams, requestOptions);

    CustomerModel customerModel = customerRepository.findByCustomerId(payload.getCustomerId());

    UserWorkspaceModel setterWorkspaceModel = null;

    float setterCommissionPercent = 0.0F;

    CommSplitScenarioType scenarioType = CommSplitScenarioType.CLOSER_ONLY;

    List<TeamModel> closerManagers = teamRepository.findManyTeamMembers(
      closerWorkspaceModel.getUser().getId(),
      closerWorkspaceModel.getWorkspace().getId()
    );

    List<TeamModel> setterManagers = Collections.emptyList();
    if (customerModel != null && Boolean.TRUE.equals(customerModel.getSetterInvolved())) {
      setterWorkspaceModel = userWorkspaceRepository.findOneByUserId(
        customerModel.getSetter().getId(),
        payload.getWorkspaceId()
      );

      if (setterWorkspaceModel != null) {
        setterCommissionPercent = setterWorkspaceModel.getCommission().getCommission();
        scenarioType = CommSplitScenarioType.CLOSER_AND_SETTER;

        setterManagers = teamRepository.findManyTeamMembers(
          setterWorkspaceModel.getUser().getId(),
          setterWorkspaceModel.getWorkspace().getId()
        );
      }
    }

    // find customer from stripe before below
    assert customerModel != null;
    CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
      .setName(customerModel.getName())
      .setEmail(customerModel.getEmail())
      .setDescription(customerModel.getDescription())
      .build();

    Customer customer = Customer.create(customerCreateParams, requestOptions);

    Double invoice_amount = payload.getQty() > 0 ? Double.valueOf(payload.getQty() * payload.getUnitPrice()) : payload.getUnitPrice();

    // CALCULATE COMMISSIONS
    CalcRequest calcRequest = CalcRequest.builder()
      .revenue(invoice_amount)
      .scenario(scenarioType)
      .closerPercent(commissionSettingModel.getCommission())
      .closerManager(closerManagers)
      .setterManager(setterManagers)
      .build();

    if (calcRequest.getScenario().equals(CommSplitScenarioType.CLOSER_AND_SETTER)) {
      calcRequest.setSetterPercent(setterCommissionPercent);
    }

    CommissionRecord commissionRecord = calculatorService.displayCommissions(calcRequest);

    long applicationFee = Double.valueOf(AmountDto.getAmountDto(commissionRecord.paydaiApplicationFee(), payload.getCurrency()).getLgUnitAmt()).longValue();

    long dueDate = payload.getDueDate().atZone(ZoneOffset.UTC).toEpochSecond();

    InvoiceCreateParams invoiceCreateParams =
      InvoiceCreateParams.builder()
        .setCustomer(customer.getId())
        .setCollectionMethod(InvoiceCreateParams.CollectionMethod.SEND_INVOICE)
        .setDueDate(dueDate)
        .setApplicationFeeAmount(applicationFee)
        .build();

    Invoice invoice = Invoice.create(invoiceCreateParams, requestOptions);

    InvoiceItemCreateParams params =
      InvoiceItemCreateParams.builder()
        .setCustomer(customer.getId())
        .setPrice(price.getId())
        .setQuantity((long) payload.getQty())
        .setInvoice(invoice.getId())
        .build();

    InvoiceItem invoiceItem = InvoiceItem.create(params, requestOptions);

    ProductModel productModel = productRepository.save(
      ProductModel.builder()
        .item(payload.getProductName())
        .qty(payload.getQty())
        .unitPrice(payload.getUnitPrice())
        .description(payload.getProductDescription())
        .stripeProductId(product.getId())
        .build()
    );

    LocalDateTime localDateTime = LocalDateTime.now();
    ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
    long milliseconds = zonedDateTime.toInstant().toEpochMilli();

    InvoiceModel buildInvoice = InvoiceModel.builder()
      .subject(payload.getSubject())
      .currency(payload.getCurrency())
      .amount(invoice_amount)
      .unit(amountDto.getSmUnit())
      .dueDate(payload.getDueDate())
      .status(InvoiceStatus.CREATED)
      .invoiceCode("INV" + milliseconds)
      .applicationFee(commissionRecord.paydaiApplicationFee())
      .platformFee(commissionRecord.paydaiTotalComm())
      .commSplitScenario(CommSplitScenarioType.CLOSER_ONLY)
      .snapshotCommCloserPercent(commissionSettingModel.getCommission())
      .snapshotCloserFeePercent(commissionRecord.paydaiFeeCloserPercent())
      .snapshotCommCloserNet(commissionRecord.closerNet())
      .snapshotCommCloser(commissionRecord.closerCommission())
      .snapshotCommSetterPercent(0.0F)
      .snapshotMerchantFeePercent(commissionRecord.paydaiFeeMerchantPercent())
      .snapshotCommInterval(commissionSettingModel.getInterval())
      .snapshotCommIntervalUnit(commissionSettingModel.getIntervalUnit())
      .stripeInvoiceItem(invoiceItem.getInvoice())
      .stripeInvoiceId(invoice.getId())
      .stripeInvoicePdf(invoice.getInvoicePdf())
      .stripeInvoiceHostedUrl(invoice.getHostedInvoiceUrl())
      .customer(customerModel)
      .product(productModel)
      .userWorkspace(closerWorkspaceModel)
      .workspace(WorkspaceModel.builder().id(payload.getWorkspaceId()).build())
      .build();

    if (setterWorkspaceModel != null) {
      buildInvoice.setSnapshotCommSetterPercent(setterCommissionPercent);
      buildInvoice.setSnapshotSetterFeePercent(commissionRecord.paydaiFeeSetterPercent());
      buildInvoice.setSnapshotCommSetterNet(commissionRecord.setterNet());
      buildInvoice.setSnapshotCommSetter(commissionRecord.setterCommission());
      buildInvoice.setCommSplitScenario(CommSplitScenarioType.CLOSER_AND_SETTER);
    }

    InvoiceModel invoiceModel = repository.save(buildInvoice);

    if (commissionRecord != null && !commissionRecord.closerManagersCommissions().isEmpty()) {
      commissionRecord
        .closerManagersCommissions()
        .forEach(invoiceManagerModel -> {
          invoiceManagerModel.setInvoice(invoiceModel);
          invoiceManagerRepository.save(invoiceManagerModel);
        });
    }

      if (commissionRecord != null && !commissionRecord.setterManagersCommissions().isEmpty()) {
        commissionRecord
          .setterManagersCommissions()
          .forEach(invoiceManagerModel -> {
            invoiceManagerModel.setInvoice(invoiceModel);
            invoiceManagerRepository.save(invoiceManagerModel);
          });
      }
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
      invoice.finalizeInvoice(InvoiceFinalizeInvoiceParams.builder().build(), requestOptions);

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
  public JapiResponse filterInvoice(UUID workspaceId, List<InvoiceStatus> status) {
    List<InvoiceModel> invoiceModels;
    if (status.isEmpty()) {
      invoiceModels = repository.findByWorkspaceInvoices(workspaceId);
    } else {
      invoiceModels = repository.findByFilterWorkspaceInvoices(workspaceId, status.stream().map(Enum::name).toList());
    }

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
        double managerRefund = Optional.ofNullable(refundRepository.findTotalRefundPaid(srId, invoiceModel.getWorkspace().getId(), invoiceModel.getId())).orElse(0.0);
        InvoiceSalesRepDto invoiceSalesRepDto = InvoiceSalesRepDto.getInvoiceSalesRepDto(srId, credit, name, "manager", managerRefund);
        salesReps.add(invoiceSalesRepDto);
      }

    double closerRefund = Optional.ofNullable(refundRepository.findTotalRefundPaid(closer.getId(), invoiceModel.getWorkspace().getId(), invoiceModel.getId())).orElse(0.0);
    salesReps.add(InvoiceSalesRepDto.getInvoiceSalesRepDto(closer.getId(), invoiceModel.getSnapshotCommCloserNet(), closer.getFirstName(), "closer", closerRefund));

    if (setter != null) {
      double setterRefund = Optional.ofNullable(refundRepository.findTotalRefundPaid(setter.getId(), invoiceModel.getWorkspace().getId(), invoiceModel.getId())).orElse(0.0);
      salesReps.add(InvoiceSalesRepDto.getInvoiceSalesRepDto(setter.getId(), invoiceModel.getSnapshotCommCloserNet(), setter.getFirstName(), "setter", setterRefund));
    }

    return JapiResponse.success(salesReps);
  }

  @Override
  @TryCatchException
  public JapiResponse cancelInvoice(String invoiceCode) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserModel userModel = (UserModel) authentication.getPrincipal();

    InvoiceModel invoiceModel = repository.findByInvoiceCode(invoiceCode);

    if (invoiceModel == null) throw new NotFoundException("invoice");

    boolean notAllowed = !invoiceModel.getUserWorkspace().getUser().getId().equals(userModel.getId())
                        && !invoiceModel.getWorkspace().getOwner().getId().equals(userModel.getId());

    if (notAllowed) throw new ForbiddenException("You do not have this kind of permission");

    if (invoiceModel.getStatus().equals(InvoiceStatus.CANCELED)) throw new ApiRequestException("Invoice already canceled!");

    if (!invoiceModel.getStatus().equals(InvoiceStatus.SENT)) throw new ApiRequestException("You cannot cancel already sent invoice");

    Invoice resource;
    try {
      RequestOptions requestOptions = RequestOptions.builder().setStripeAccount(invoiceModel.getWorkspace().getOwner().getStripeId()).build();
      resource = Invoice.retrieve(invoiceModel.getStripeInvoiceId(), requestOptions);
      InvoiceVoidInvoiceParams invoiceVoidInvoiceParams = InvoiceVoidInvoiceParams.builder().build();
      resource = resource.voidInvoice(invoiceVoidInvoiceParams, requestOptions);
    } catch (StripeException e) {
      throw new com.paydai.api.domain.exception.StripeException(e.getMessage());
    }

    invoiceModel.setCanceled(true);

    invoiceModel.setStatus(InvoiceStatus.CANCELED);

    invoiceModel.setStripeInvoiceStatus(resource.getStatus());

    InvoiceModel invoiceModel1 = repository.save(invoiceModel);

    return JapiResponse.success(getInvoiceRecordDetails(invoiceModel1));
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
