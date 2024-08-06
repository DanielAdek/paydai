package com.paydai.api.application;

import com.paydai.api.application.constant.WebhookConstant;
import com.paydai.api.domain.exception.ApiRequestException;
import com.paydai.api.domain.repository.WebhookRepository;
import com.paydai.api.domain.service.PayoutLedgerService;
import com.paydai.api.domain.service.WebhookService;
import com.paydai.api.presentation.dto.webhook.WebhookRegister;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.WebhookEndpointCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {
  private final WebhookRepository repository;
  private final WebhookConstant webhookConstant;
  private final PayoutLedgerService payoutLedgerService;
  private final InvoiceServiceImpl invoiceService;

  @Override
  public JapiResponse handleInvoiceEventConnectAccount(String payload, Event event) {
    try {
      EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();

      StripeObject stripeObject;

      if (dataObjectDeserializer.getObject().isPresent()) {
//        Application application = (I) dataObjectDeserializer.getObject().get();
        stripeObject = dataObjectDeserializer.getObject().get();
//        String connectedAccountId = event.getAccount();
      } else {
        throw new ApiRequestException("Deserialization error");
      }

//      if (event.getType().equals(webhookConstant.invoice_created)) {
//        System.out.println("The invoice create from connect called!");
//        processRequestFromInvoiceEvent(stripeObject);
//      }

      if (event.getType().equals(webhookConstant.invoice_finalize)) {
        System.out.println("The invoice finalize from connect called!");
      }

      if (event.getType().equals(webhookConstant.invoice_sent)) {
        System.out.println("The invoice sent from connect called!");
      }

      if (event.getType().equals(webhookConstant.invoice_payment_succeeded)) {
        log.info("This invoice payment success", stripeObject);
        log.info(String.valueOf(stripeObject));
      }

      if (event.getType().equals(webhookConstant.invoice_paid)) {
        log.info("This invoice paid", stripeObject);
        log.info(String.valueOf(stripeObject));
      }

      else {
        log.warn("Unhandled event type: " + event.getType());
      }

      return JapiResponse.success(null);
    } catch (Exception e) { throw e; }
  }

  @Override
  public JapiResponse handleTransferEventAccount(String payload, Event event) throws StripeException {
    try {
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
    } catch (Exception e) { throw e; }
  }

  @Override
  public JapiResponse handleBalanceEventConnectAccount(String payload, Event event) {
    try {
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
        log.info("This is the object stripe upon balance", stripeObject);
      }

      else {
        log.warn("Unhandled event type: " + event.getType());
      }

      return JapiResponse.success(null);
    } catch (Exception e) { throw e; }
  }

  private void processRequestFromBalanceEvent(StripeObject payload) {
    System.out.println(payload);
  }

  private void processRequestFromInvoiceEvent(StripeObject payload) {
    System.out.println(payload);
  }
}
