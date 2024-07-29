package com.paydai.api.application;

import com.paydai.api.domain.exception.NotFoundException;
import com.paydai.api.domain.model.*;
import com.paydai.api.domain.repository.*;
import com.paydai.api.domain.service.InvoiceService;
import com.paydai.api.presentation.dto.invoice.InvoiceDtoMapper;
import com.paydai.api.presentation.dto.invoice.InvoiceRecord;
import com.paydai.api.presentation.request.InvoiceRequest;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
  private final InvoiceRepository repository;
  private final InvoiceDtoMapper invoiceDtoMapper;
  private final ProductRepository productRepository;
  private final CustomerRepository customerRepository;
  private final UserWorkspaceRepository userWorkspaceRepository;

  @Override
  public JapiResponse create(InvoiceRequest payload) throws StripeException {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      UserModel userModel = (UserModel) authentication.getPrincipal();

      ProductCreateParams productCreateParams = ProductCreateParams.builder().setName(payload.getProductName()).build();

      Product product = Product.create(productCreateParams);

      PriceCreateParams priceCreateParams = PriceCreateParams.builder()
        .setProduct(product.getId())
        .setUnitAmount(payload.getUnitPrice().longValue())
        .setCurrency(payload.getCurrency())
        .build();

      Price price = Price.create(priceCreateParams);

      CustomerModel customerModel = customerRepository.findByCustomerId(payload.getCustomerId());

      // find customer from stripe before below
      CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
        .setName(customerModel.getName())
        .setEmail(customerModel.getEmail())
        .setDescription(customerModel.getDescription())
        .build();

      Customer customer = Customer.create(customerCreateParams);

      InvoiceCreateParams invoiceCreateParams =
        InvoiceCreateParams.builder()
          .setCustomer(customer.getId())
          .setCollectionMethod(InvoiceCreateParams.CollectionMethod.SEND_INVOICE) // set if you want to send invoice
          .setDaysUntilDue(30L) // set if you want Stripe to mark an invoice as past due, you must add the days_until_due parameter
//          .setOnBehalfOf(userWorkspaceModel.getWorkspace().getOwner().getStripeId())
//          .setApplicationFeeAmount(10L)
          .build();

      Invoice invoice = Invoice.create(invoiceCreateParams);

      InvoiceItemCreateParams params =
        InvoiceItemCreateParams.builder()
          .setCustomer(customer.getId())
          .setPrice(price.getId())
          .setInvoice(invoice.getId())
          .build();

      InvoiceItem invoiceItem = InvoiceItem.create(params);

      ProductModel productModel = productRepository.save(
        ProductModel.builder()
          .item(payload.getProductName())
          .qty(payload.getQty())
          .unitPrice(payload.getUnitPrice())
          .description(payload.getProductDescription())
          .stripeProductId(product.getId())
          .build()
      );

      UserWorkspaceModel closerWorkspaceModel = userWorkspaceRepository.findOneByUserId(userModel.getId(), payload.getWorkspaceId());

      CommissionSettingModel commissionSettingModel = closerWorkspaceModel.getCommission(); // commission of the creator of invoice (closer)

      UserWorkspaceModel setterWorkspaceModel = null;
      if (customerModel.getSetterInvolved()) {
        setterWorkspaceModel = userWorkspaceRepository.findOneByUserId(customerModel.getCreator().getId(), payload.getWorkspaceId());
      }

      LocalDateTime localDateTime = LocalDateTime.now();
      ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
      long milliseconds = zonedDateTime.toInstant().toEpochMilli();


      InvoiceModel buildInvoice = InvoiceModel.builder()
        .stripeInvoiceId(invoice.getId())
        .subject(payload.getSubject())
        .currency(payload.getCurrency())
        .customer(customerModel)
        .dueDate(payload.getDueDate())
        .stripeInvoiceItem(invoiceItem.getInvoice())
        .userWorkspace(closerWorkspaceModel)
        .workspace(WorkspaceModel.builder().id(payload.getWorkspaceId()).build())
        .product(productModel)
        .status(InvoiceStatus.CREATED)
        .snapshotCommCloserPercent(commissionSettingModel.getCommission())
        .snapshotCommSetterPercent(0.0F)
        .snapshotCommInterval(commissionSettingModel.getInterval())
        .snapshotCommIntervalUnit(commissionSettingModel.getIntervalUnit())
        .snapshotCommAggregate(commissionSettingModel.getAggregate())
        .invoiceCode("INV" + milliseconds)
        .stripeInvoicePdf(invoice.getInvoicePdf())
        .stripeInvoiceHostedUrl(invoice.getHostedInvoiceUrl())
        .build();

      if (setterWorkspaceModel != null) {
        buildInvoice.setSnapshotCommSetterPercent(setterWorkspaceModel.getCommission().getCommission()); // commission of the creator of customer (setter)
      }

      InvoiceModel invoiceModel = repository.save(buildInvoice);

      InvoiceRecord invoiceRecord = invoiceDtoMapper.apply(invoiceModel);

      return JapiResponse.success(invoiceRecord);
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

      List<InvoiceRecord> invoiceRecords = new ArrayList<>();
      if (!invoiceModels.isEmpty()) {
        invoiceRecords = invoiceModels.stream().map(invoiceDtoMapper).toList();
      }

      return JapiResponse.success(invoiceRecords);
    } catch (Exception e) { throw e; }
  }

  @Override
  public JapiResponse getInvoice(String invoiceCode) {
    try {
      InvoiceModel invoiceModel = repository.findByInvoiceCode(invoiceCode);

      if (invoiceModel == null) throw new NotFoundException("Invalid invoice code");

      InvoiceRecord invoiceRecord = invoiceDtoMapper.apply(invoiceModel);

      return JapiResponse.success(invoiceRecord);
    } catch (Exception e) { throw e; }
  }

  @Override
  public JapiResponse finalizeInvoice(String invoiceCode) throws StripeException {
    try {
      InvoiceModel invoiceModel = repository.findByInvoiceCode(invoiceCode);

      if (invoiceModel == null) throw new NotFoundException("Invoice code is invalid");

      Invoice invoice = Invoice.retrieve(invoiceModel.getStripeInvoiceId());

      InvoiceFinalizeInvoiceParams invoiceFinalizeInvoiceParams = InvoiceFinalizeInvoiceParams.builder().build();

      return JapiResponse.success(invoiceFinalizeInvoiceParams);
    } catch (Exception e) { throw e; }
  }

  @Override
  public JapiResponse sendInvoice(String invoiceCode) throws StripeException {
    try {
      InvoiceModel invoiceModel = repository.findByInvoiceCode(invoiceCode);

      if (invoiceModel == null) throw new NotFoundException("Invoice code is invalid");

      Invoice invoice = Invoice.retrieve(invoiceModel.getStripeInvoiceId());

      InvoiceSendInvoiceParams invoiceSendInvoiceParams = InvoiceSendInvoiceParams.builder().build();

      return JapiResponse.success(invoiceSendInvoiceParams);
    } catch (Exception e) { throw e; }
  }
}
