package com.paydai.api.application;

import com.paydai.api.application.constant.WebhookConstant;
import com.paydai.api.domain.annotation.TryCatchException;
import com.paydai.api.domain.exception.ApiRequestException;
import com.paydai.api.domain.model.InvoiceStatus;
import com.paydai.api.domain.repository.InvoiceRepository;
import com.paydai.api.domain.repository.WebhookRepository;
import com.paydai.api.domain.service.TransactionService;
import com.paydai.api.domain.service.WebhookService;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {
  private final WebhookRepository repository;
  private final WebhookConstant webhookConstant;
  private final InvoiceRepository invoiceRepository;
  private final TransactionService payoutLedgerService;

  @Override
  @TryCatchException
  public JapiResponse handleInvoiceEventConnectAccount(String payload, Event event) throws StripeException {
    EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();

    Invoice invoice;

    if (dataObjectDeserializer.getObject().isPresent()) {
      invoice = (Invoice) dataObjectDeserializer.getObject().get();
    } else {
      throw new ApiRequestException("Deserialization error");
    }

    if (event.getType().equals(webhookConstant.invoice_finalize)) {
      System.out.println("The invoice finalize from connect called!");
    }

    if (event.getType().equals(webhookConstant.invoice_sent)) {
      System.out.println("The invoice sent from connect called!");
    }

    if (event.getType().equals(webhookConstant.invoice_payment_succeeded)) {
      log.info("This invoice payment success");
      // update invoice to paid
      invoiceRepository.updateInvoiceStatus(invoice.getId(), invoice.getStatus(), InvoiceStatus.PAID.toString());

      // transfer fund to sales rep
      payoutLedgerService.transferToSalesRep(invoice);
    }

    if (event.getType().equals(webhookConstant.invoice_paid)) {}

    return JapiResponse.success(null);
  }

  @Override
  @TryCatchException
  public JapiResponse handleTransferEventAccount(String payload, Event event) throws StripeException {
    EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();

    StripeObject stripeObject;

    if (dataObjectDeserializer.getObject().isPresent()) {
      stripeObject = dataObjectDeserializer.getObject().get();
    } else {
      throw new ApiRequestException("Deserialization error");
    }

    if (event.getType().equals(webhookConstant.transfer_created)) {
      log.info("This is triggered", stripeObject);
//        payoutLedgerService.transferToSalesRep(stripeObject);
    }

    if (event.getType().equals(webhookConstant.transfer_reversed)) {
      processRequestFromBalanceEvent(stripeObject);
    }
    else {
      log.warn("Unhandled event type: " + event.getType());
    }

    return JapiResponse.success(null);
  }

  @Override
  @TryCatchException
  public JapiResponse handleBalanceEventConnectAccount(String payload, Event event) {
    EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();

    StripeObject stripeObject;

    if (dataObjectDeserializer.getObject().isPresent()) {
      stripeObject = dataObjectDeserializer.getObject().get();
    } else {
      throw new ApiRequestException("Deserialization error");
    }

    if (event.getType().equals(webhookConstant.balance)) {
      log.info("The balance is available");
      // handle transfer here
      log.info(String.valueOf(stripeObject));
    }

    else {
      log.warn("Unhandled event type: " + event.getType());
    }

    return JapiResponse.success(null);
  }

  private void processRequestFromBalanceEvent(StripeObject payload) {
    System.out.println(payload);
  }

  private void processRequestFromInvoiceEvent(StripeObject payload) {
    System.out.println(payload);
  }
}
