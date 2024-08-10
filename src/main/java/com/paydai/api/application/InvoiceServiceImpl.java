package com.paydai.api.application;

import com.paydai.api.domain.exception.NotFoundException;
import com.paydai.api.domain.model.*;
import com.paydai.api.domain.repository.*;
import com.paydai.api.domain.service.InvoiceService;
import com.paydai.api.presentation.dto.commission.CommissionRecord;
import com.paydai.api.presentation.dto.invoice.InvoiceDto;
import com.paydai.api.presentation.dto.invoice.InvoiceDtoMapper;
import com.paydai.api.presentation.dto.invoice.InvoiceRecord;
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

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
  private final InvoiceRepository repository;
  private final InvoiceDtoMapper invoiceDtoMapper;
  private final ProductRepository productRepository;
  private final CustomerRepository customerRepository;
  private final CalculatorServiceImpl calculatorService;
  private final UserWorkspaceRepository userWorkspaceRepository;

  @Override
  public JapiResponse create(InvoiceRequest payload) throws StripeException {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      UserModel userModel = (UserModel) authentication.getPrincipal();

      UserWorkspaceModel closerWorkspaceModel = userWorkspaceRepository.findOneByUserId(userModel.getId(), payload.getWorkspaceId());

      CommissionSettingModel commissionSettingModel = closerWorkspaceModel.getCommission();

      String connectedAccountId = closerWorkspaceModel.getWorkspace().getOwner().getStripeId();

      RequestOptions requestOptions = RequestOptions.builder().setStripeAccount(connectedAccountId).build();

      ProductCreateParams productCreateParams = ProductCreateParams.builder()
        .setName(payload.getProductName())
        .setDescription(payload.getProductDescription())
        .build();

      Product product = Product.create(productCreateParams, requestOptions);

      PriceCreateParams priceCreateParams = PriceCreateParams.builder()
        .setProduct(product.getId())
        .setUnitAmount(payload.getUnitPrice().longValue() * 100)
        .setCurrency(payload.getCurrency())
        .build();

      Price price = Price.create(priceCreateParams, requestOptions);

      CustomerModel customerModel = customerRepository.findByCustomerId(payload.getCustomerId());

      UserWorkspaceModel setterWorkspaceModel = null;

      float setterCommission = 0.0F;

      CommSplitScenarioType scenarioType = CommSplitScenarioType.CLOSER_ONLY;

      if (customerModel != null) {
        Boolean setterInvolved = customerModel.getSetterInvolved();
        if (setterInvolved != null && setterInvolved) {
          setterWorkspaceModel = userWorkspaceRepository.findOneByUserId(customerModel.getCreator().getId(), payload.getWorkspaceId());

          if (setterWorkspaceModel != null) {
            setterCommission = setterWorkspaceModel.getCommission().getCommission();
            scenarioType = CommSplitScenarioType.CLOSER_AND_SETTER;
          }
        }
      }

      // find customer from stripe before below
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
        .build();

      if (calcRequest.getScenario().equals(CommSplitScenarioType.CLOSER_AND_SETTER)) {
        calcRequest.setSetterPercent(setterCommission);
      }

      CommissionRecord commissionRecord = calculatorService.displayCommissions(calcRequest);

      long applicationFee = Double.valueOf(commissionRecord.paydaiApplicationFee()).longValue();

      long now = Instant.now().plus(1, ChronoUnit.MINUTES).getEpochSecond();

      InvoiceCreateParams invoiceCreateParams =
        InvoiceCreateParams.builder()
          .setCustomer(customer.getId())
          .setCollectionMethod(InvoiceCreateParams.CollectionMethod.SEND_INVOICE) // set if you want to send invoice
//          .setDaysUntilDue(30L) // set if you want Stripe to mark an invoice as past due, you must add the days_until_due parameter
          .setDueDate(now) // set if you want Stripe to mark an invoice as past due, you must add the days_until_due parameter
          .setApplicationFeeAmount(applicationFee * 100)
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
        .stripeInvoiceId(invoice.getId())
        .subject(payload.getSubject())
        .currency(payload.getCurrency())
        .amount(invoice_amount)
        .amtSmUnit(100)
        .customer(customerModel)
        .dueDate(payload.getDueDate())
        .stripeInvoiceItem(invoiceItem.getInvoice())
        .userWorkspace(closerWorkspaceModel)
        .workspace(WorkspaceModel.builder().id(payload.getWorkspaceId()).build())
        .product(productModel)
        .status(InvoiceStatus.CREATED)
        .snapshotCommCloserPercent(commissionSettingModel.getCommission())
        .snapshotCloserFeePercent(commissionRecord.paydaiFeeCloserPercent())
        .snapshotCommSetterPercent(0.0F)
        .applicationFee(commissionRecord.paydaiApplicationFee())
        .platformFee(commissionRecord.paydaiTotalComm())
        .snapshotMerchantFeePercent(commissionRecord.paydaiFeeMerchantPercent())
        .snapshotCommCloserNet(commissionRecord.closerNet())
        .snapshotCommInterval(commissionSettingModel.getInterval())
        .snapshotCommIntervalUnit(commissionSettingModel.getIntervalUnit())
        .snapshotCommAggregate(commissionSettingModel.getAggregate())
        .commSplitScenario(CommSplitScenarioType.CLOSER_ONLY)
        .invoiceCode("INV" + milliseconds)
        .stripeInvoicePdf(invoice.getInvoicePdf())
        .stripeInvoiceHostedUrl(invoice.getHostedInvoiceUrl())
        .build();

      if (setterWorkspaceModel != null) {
        buildInvoice.setSnapshotCommSetterPercent(setterCommission);
        buildInvoice.setSnapshotSetterFeePercent(commissionRecord.paydaiFeeSetterPercent());
        buildInvoice.setSnapshotCommSetterNet(commissionRecord.setterNet());
        buildInvoice.setCommSplitScenario(CommSplitScenarioType.CLOSER_AND_SETTER);
      }

      InvoiceModel invoiceModel = repository.save(buildInvoice);
      return JapiResponse.success(getInvoiceRecordDetails(invoiceModel));
    } catch (Exception e) { throw e; }
  }

  @Override
  public JapiResponse getInvoiceToCustomer(UUID customerId) {
    try {
      return JapiResponse.success(null);
    } catch (Exception e) { throw e; }
  }

  @Override
  public JapiResponse getWorkspaceInvoicesToCustomers(UUID workspaceId) {
    try {
      List<InvoiceModel> invoiceModels = repository.findByWorkspaceInvoices(workspaceId);
      List<InvoiceRecord> invoiceDtos = new ArrayList<>();
      if (!invoiceModels.isEmpty()) {
        invoiceDtos = invoiceModels.stream().map(this::getInvoiceRecordDetails).toList();
      }
      return JapiResponse.success(invoiceDtos);
    } catch (Exception e) { throw e; }
  }

  @Override
  public JapiResponse getInvoice(String invoiceCode) {
    try {
      InvoiceModel invoiceModel = repository.findByInvoiceCode(invoiceCode);

      if (invoiceModel == null) throw new NotFoundException("Invalid invoice code");

      return JapiResponse.success(getInvoiceRecordDetails(invoiceModel));
    } catch (Exception e) { throw e; }
  }

  @Override
  public JapiResponse finalizeInvoice(String invoiceCode) throws StripeException {
    try {
      InvoiceModel invoiceModel = validateInvoice(invoiceCode);

      String connectedAccountId = invoiceModel.getUserWorkspace().getWorkspace().getOwner().getStripeId();

      RequestOptions requestOptions = RequestOptions.builder().setStripeAccount(connectedAccountId).build();

      Invoice invoice = Invoice.retrieve(invoiceModel.getStripeInvoiceId(), requestOptions);

      InvoiceFinalizeInvoiceParams invoiceFinalizeInvoiceParams = InvoiceFinalizeInvoiceParams.builder().build();

      invoice = invoice.finalizeInvoice(invoiceFinalizeInvoiceParams, requestOptions);

      Map<String, Object> response = invoiceDetails(invoice, invoiceModel);

      repository.updateInvoiceByInvoiceCode(invoiceCode, response.toString(), invoice.getHostedInvoiceUrl(), invoice.getInvoicePdf(), invoice.getStatus(), String.valueOf(InvoiceStatus.FINALIZED));

      return JapiResponse.success(response);
    } catch (Exception e) { throw e; }
  }

  @Override
  public JapiResponse sendInvoice(String invoiceCode) throws StripeException {
    try {
      InvoiceModel invoiceModel = validateInvoice(invoiceCode);

      String connectedAccountId = invoiceModel.getUserWorkspace().getWorkspace().getOwner().getStripeId();

      RequestOptions requestOptions = RequestOptions.builder().setStripeAccount(connectedAccountId).build();

      Invoice invoice = Invoice.retrieve(invoiceModel.getStripeInvoiceId(), requestOptions);

      InvoiceSendInvoiceParams invoiceSendInvoiceParams = InvoiceSendInvoiceParams.builder().build();

      invoice = invoice.sendInvoice(invoiceSendInvoiceParams, requestOptions);

      repository.updateInvoiceStatus(invoiceCode, invoice.getStatus(), InvoiceStatus.SENT.toString());

      return JapiResponse.success(invoiceDetails(invoice, invoiceModel));
    } catch (Exception e) { throw e; }
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

    InvoiceDto invoiceDto = InvoiceDto.getInvoiceDto(invoiceModel, commissionRecord);

    return invoiceDtoMapper.apply(invoiceDto);
  }
}
