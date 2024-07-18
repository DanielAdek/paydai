package com.paydai.api.application;

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
  private final CommSettingRepository commSettingRepository;

  @Override
  public JapiResponse create(InvoiceRequest payload) throws StripeException {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      UserModel userModel = (UserModel) authentication.getPrincipal();

      UserWorkspaceModel userWorkspaceModel = userWorkspaceRepository.findOneByUserId(userModel.getId(), payload.getWorkspaceId());

      CommissionSettingModel commissionSettingModel = userWorkspaceModel.getCommission();

      ProductCreateParams productCreateParams = ProductCreateParams.builder().setName(payload.getProductName()).build();

      Product product = Product.create(productCreateParams);

      PriceCreateParams priceCreateParams = PriceCreateParams.builder()
          .setProduct(product.getId())
          .setUnitAmount(payload.getUnitPrice().longValue())
          .setCurrency(payload.getCurrency())
          .build();

      Price price = Price.create(priceCreateParams);

      CustomerModel customerModel = customerRepository.findByCustomerId(payload.getCustomerId());

      CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
          .setName(customerModel.getName())
          .setEmail(customerModel.getEmail())
          .setDescription(customerModel.getDescription())
          .build();

      Customer customer = Customer.create(customerCreateParams);

      InvoiceCreateParams invoiceCreateParams =
        InvoiceCreateParams.builder()
          .setCustomer(customer.getId())
//          .setCollectionMethod(InvoiceCreateParams.CollectionMethod.SEND_INVOICE) // set if you want to send invoice
//          .setDaysUntilDue(30L) // set if you want Stripe to mark an invoice as past due, you must add the days_until_due parameter
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

      LocalDateTime localDateTime = LocalDateTime.now();
      ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
      long milliseconds = zonedDateTime.toInstant().toEpochMilli();


      InvoiceModel invoiceModel = repository.save(
        InvoiceModel.builder()
          .stripeInvoiceId(invoice.getId())
          .subject(payload.getSubject())
          .currency(payload.getCurrency())
          .customer(customerModel)
          .dueDate(payload.getDueDate())
          .stripeInvoiceItem(invoiceItem.getInvoice())
          .userWorkspace(userWorkspaceModel)
          .workspace(WorkspaceModel.builder().id(payload.getWorkspaceId()).build())
          .product(productModel)
          .status(InvoiceStatus.CREATED)
          .snapshotCommPercent(commissionSettingModel.getCommission())
          .snapshotCommInterval(commissionSettingModel.getInterval())
          .snapshotCommIntervalUnit(commissionSettingModel.getIntervalUnit())
          .snapshotCommAggregate(commissionSettingModel.getAggregate())
//          .merchantFee()
          .invoiceCode("INV" + milliseconds)
          .stripeInvoicePdf(invoice.getInvoicePdf())
          .stripeInvoiceHostedUrl(invoice.getHostedInvoiceUrl())
          .build()
      );

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
    return null;
  }
}
