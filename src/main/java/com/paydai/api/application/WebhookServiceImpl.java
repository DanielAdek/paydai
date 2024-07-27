package com.paydai.api.application;

import com.paydai.api.application.constant.WebhookConstant;
import com.paydai.api.domain.exception.ApiRequestException;
import com.paydai.api.domain.repository.WebhookRepository;
import com.paydai.api.domain.service.WebhookService;
import com.paydai.api.presentation.dto.webhook.WebhookRegister;
import com.paydai.api.presentation.response.JapiResponse;
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

  @Override
  public JapiResponse handleConnectEvents(String payload, Event event) {
    try {
      EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();

      StripeObject stripeObject;

      if (dataObjectDeserializer.getObject().isPresent()) {
        Application application = (Application) dataObjectDeserializer.getObject().get();
        stripeObject = dataObjectDeserializer.getObject().get();
        String connectedAccountId = event.getAccount();
      } else {
        throw new ApiRequestException("Deserialization error");
      }

      if (event.getType().equals(webhookConstant.invoice_created)) {
        System.out.println("The invoice create from connect called!");
        handleEventCallSucceeded(stripeObject);
      }

      if (event.getType().equals(webhookConstant.invoice_finalize)) {
        System.out.println("The invoice finalize from connect called!");
      }

      if (event.getType().equals(webhookConstant.invoice_sent)) {
        System.out.println("The invoice sent from connect called!");
      }
      else {
        log.warn("Unhandled event type: " + event.getType());
      }

      return JapiResponse.success(null);
    } catch (Exception e) { throw e; }
  }

  @Override
  public JapiResponse handleAccountEvents(String payload, Event event) {
    try {
      EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();

      StripeObject stripeObject;

      if (dataObjectDeserializer.getObject().isPresent()) {
        stripeObject = dataObjectDeserializer.getObject().get();
      } else {
        throw new ApiRequestException("Deserialization error");
      }

      if (event.getType().equals(webhookConstant.invoice_created)) {
        System.out.println("The invoice create from account called!");
        handleEventCallSucceeded(stripeObject);
      }

      if (event.getType().equals(webhookConstant.invoice_finalize)) {
        System.out.println("The invoice finalize from account called!");
      }

      if (event.getType().equals(webhookConstant.invoice_sent)) {
        System.out.println("The invoice sent from account called!");
      }
      else {
        log.warn("Unhandled event type: " + event.getType());
      }

      return JapiResponse.success(null);
    } catch (Exception e) { throw e; }
  }

  private void handleEventCallSucceeded(StripeObject payload) {
    System.out.println(payload);
  }
}
