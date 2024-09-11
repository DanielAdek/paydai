package com.paydai.api.application;

import com.paydai.api.domain.model.*;
import com.paydai.api.domain.repository.*;
import com.paydai.api.presentation.dto.AmountDto;
import com.paydai.api.presentation.dto.commission.CommissionRecord;
import com.paydai.api.presentation.dto.invoice.CommissionData;
import com.paydai.api.presentation.request.CalcRequest;
import com.paydai.api.presentation.request.InvoiceRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.paydai.api.domain.service.InvoiceHelperService;
import com.stripe.net.RequestOptions;
import com.stripe.param.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceHelperServiceImpl implements InvoiceHelperService {
  private final InvoiceRepository repository;
  private final TeamRepository teamRepository;
  private final ProductRepository productRepository;
  private final UserWorkspaceRepository userWorkspaceRepository;
  private final InvoiceManagerRepository invoiceManagerRepository;

  @Override
  public UserWorkspaceModel getUserWorkspaceModel(UUID userId, UUID workspaceId) {
    return userWorkspaceRepository.findOneByUserId(userId, workspaceId);
  }

  @Override
  public String getConnectedAccountId(UserWorkspaceModel closerWorkspaceModel) {
    return closerWorkspaceModel.getWorkspace().getOwner().getStripeId();
  }

  @Override
  public Product createProductInStripe(InvoiceRequest payload, String connectedAccountId) throws StripeException {
    ProductCreateParams productCreateParams = ProductCreateParams.builder()
      .setName(payload.getProductName())
      .setDescription(payload.getProductDescription())
      .build();
    RequestOptions requestOptions = RequestOptions.builder().setStripeAccount(connectedAccountId).build();
    return Product.create(productCreateParams, requestOptions);
  }

  @Override
  public Price createPriceInStripe(InvoiceRequest payload, Product product, String connectedAccountId) throws StripeException {
    AmountDto amountDto = AmountDto.getAmountDto(payload.getUnitPrice(), payload.getCurrency());
    PriceCreateParams priceCreateParams = PriceCreateParams.builder()
      .setProduct(product.getId())
      .setUnitAmount(Double.valueOf(amountDto.getLgUnitAmt()).longValue())
      .setCurrency(payload.getCurrency())
      .build();
    RequestOptions requestOptions = RequestOptions.builder().setStripeAccount(connectedAccountId).build();
    return Price.create(priceCreateParams, requestOptions);
  }

  @Override
  public List<TeamModel> getTeamMembers(UserWorkspaceModel workspaceModel) {
    return teamRepository.findManyTeamMembers(workspaceModel.getUser().getId(), workspaceModel.getWorkspace().getId());
  }

  @Override
  public CommissionData getSetterCommissionData(CustomerModel customerModel, InvoiceRequest payload, CommSplitScenarioType scenarioType) {
    if (customerModel != null && Boolean.TRUE.equals(customerModel.getSetterInvolved())) {
      return fetchSetterCommissionData(customerModel, payload, scenarioType);
    }
    return new CommissionData(Collections.emptyList(), 0.0F);
  }

  @Override
  public CommissionData fetchSetterCommissionData(CustomerModel customerModel, InvoiceRequest payload, CommSplitScenarioType scenarioType) {
    UserWorkspaceModel setterWorkspaceModel = userWorkspaceRepository.findOneByUserId(customerModel.getSetter().getId(), payload.getWorkspaceId());
    if (setterWorkspaceModel != null) {
      List<TeamModel> setterManagers = getTeamMembers(setterWorkspaceModel);
      float setterCommissionPercent = setterWorkspaceModel.getCommission().getCommission();
      return new CommissionData(setterManagers, setterCommissionPercent);
    }
    return new CommissionData(Collections.emptyList(), 0.0F);
  }

  @Override
  public Customer createCustomerInStripe(CustomerModel customerModel, String connectedAccountId) throws StripeException {
    CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
      .setName(customerModel.getName())
      .setEmail(customerModel.getEmail())
      .setDescription(customerModel.getDescription())
      .build();
    RequestOptions requestOptions = RequestOptions.builder().setStripeAccount(connectedAccountId).build();
    return Customer.create(customerCreateParams, requestOptions);
  }

  @Override
  public CalcRequest buildCalcRequest(InvoiceRequest payload, CommissionSettingModel commissionSettingModel, CommissionData commissionData, CommSplitScenarioType scenarioType, List<TeamModel> closerManagers, List<TeamModel> setterManagers) {
    Double invoiceAmount = getInvoiceAmount(payload);

    return CalcRequest.builder()
      .revenue(invoiceAmount)
      .scenario(scenarioType)
      .closerPercent(commissionSettingModel.getCommission())
      .closerManager(closerManagers)
      .setterManager(setterManagers)
      .setterPercent(commissionData.getSetterCommissionPercent())
      .build();
  }

  @Override
  public Double getInvoiceAmount(InvoiceRequest payload) {
    return payload.getQty() > 0 ? Double.valueOf(payload.getQty() * payload.getUnitPrice()) : payload.getUnitPrice();
  }

  @Override
  public Invoice createStripeInvoice(InvoiceRequest payload, Customer customer, CommissionRecord commissionRecord, String connectedAccountId) throws StripeException {
    long applicationFee = Double.valueOf(AmountDto.getAmountDto(commissionRecord.paydaiApplicationFee(), payload.getCurrency()).getLgUnitAmt()).longValue();
    long dueDate = payload.getDueDate().atZone(ZoneOffset.UTC).toEpochSecond();

    InvoiceCreateParams invoiceCreateParams = InvoiceCreateParams.builder()
      .setCustomer(customer.getId())
      .setCollectionMethod(InvoiceCreateParams.CollectionMethod.SEND_INVOICE)
      .setDueDate(dueDate)
      .setApplicationFeeAmount(applicationFee)
      .build();

    RequestOptions requestOptions = RequestOptions.builder().setStripeAccount(connectedAccountId).build();
    return Invoice.create(invoiceCreateParams, requestOptions);
  }

  @Override
  public InvoiceItem createStripeInvoiceItem(Invoice invoice, Price price, Customer customer, InvoiceRequest payload, String connectedAccountId) throws StripeException {
    InvoiceItemCreateParams params =
      InvoiceItemCreateParams.builder()
        .setCustomer(customer.getId())
        .setPrice(price.getId())
        .setQuantity((long) payload.getQty())
        .setInvoice(invoice.getId())
        .build();
    RequestOptions requestOptions = RequestOptions.builder().setStripeAccount(connectedAccountId).build();
    return InvoiceItem.create(params, requestOptions);
  }

  @Override
  public InvoiceModel saveInvoiceToDatabase(InvoiceRequest payload, AmountDto amountDto, CommissionRecord commissionRecord, UserWorkspaceModel closerWorkspaceModel, CustomerModel customerModel, CommissionSettingModel commissionSettingModel, Product product, Invoice invoice, InvoiceItem invoiceItem) {
    ProductModel productModel = saveProductToDatabase(payload, product);

    LocalDateTime localDateTime = LocalDateTime.now();
    ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
    long milliseconds = zonedDateTime.toInstant().toEpochMilli();

    InvoiceModel buildInvoice = InvoiceModel.builder()
      .subject(payload.getSubject())
      .currency(payload.getCurrency())
      .amount(getInvoiceAmount(payload))
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
      .workspace(closerWorkspaceModel.getWorkspace())
      .build();

    CommissionData setterCommissionData = fetchSetterCommissionData(customerModel, payload, CommSplitScenarioType.CLOSER_AND_SETTER);

    if (setterCommissionData != null) {
      buildInvoice.setSnapshotCommSetterPercent(setterCommissionData.getSetterCommissionPercent());
      buildInvoice.setSnapshotSetterFeePercent(commissionRecord.paydaiFeeSetterPercent());
      buildInvoice.setSnapshotCommSetterNet(commissionRecord.setterNet());
      buildInvoice.setSnapshotCommSetter(commissionRecord.setterCommission());
      buildInvoice.setCommSplitScenario(CommSplitScenarioType.CLOSER_AND_SETTER);
    }

    InvoiceModel invoiceModel = repository.save(buildInvoice);

    if (!commissionRecord.closerManagersCommissions().isEmpty()) {
      commissionRecord
        .closerManagersCommissions()
        .forEach(invoiceManagerModel -> {
          invoiceManagerModel.setInvoice(invoiceModel);
          invoiceManagerRepository.save(invoiceManagerModel);
        });
    }

    if (!commissionRecord.setterManagersCommissions().isEmpty()) {
      commissionRecord
        .setterManagersCommissions()
        .forEach(invoiceManagerModel -> {
          invoiceManagerModel.setInvoice(invoiceModel);
          invoiceManagerRepository.save(invoiceManagerModel);
        });
    }
    return invoiceModel;
  }

  @Override
  public ProductModel saveProductToDatabase(InvoiceRequest payload, Product product) {
    return productRepository.save(
      ProductModel.builder()
        .item(payload.getProductName())
        .qty(payload.getQty())
        .unitPrice(payload.getUnitPrice())
        .description(payload.getProductDescription())
        .stripeProductId(product.getId())
        .build()
    );
  }

//  @Override
//  public void saveManagerCommissions(InvoiceModel invoiceModel, CommissionRecord commissionRecord) {
//    List<CommissionDto> managerCommissions = new ArrayList<>();
//    managerCommissions.addAll(commissionRecord.getCloserManagersCommissions());
//    managerCommissions.addAll(commissionRecord.getSetterManagersCommissions());
//
//    for (CommissionDto dto : managerCommissions) {
//      InvoiceManagerModel managerModel = InvoiceManagerModel.builder()
//        .snapshotCommManagerNet(dto)
//        .commissionType(dto.getCommissionType())
//        .managerName(dto.getManagerName())
//        .invoice(invoiceModel)
//        .build();
//
//      invoiceManagerRepository.save(managerModel);
//    }
//  }
}
