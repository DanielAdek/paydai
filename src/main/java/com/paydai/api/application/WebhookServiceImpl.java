package com.paydai.api.application;

import com.paydai.api.domain.exception.ApiRequestException;
import com.paydai.api.domain.repository.WebhookRepository;
import com.paydai.api.domain.service.WebhookService;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {
  private final WebhookRepository repository;
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

      if (event.getType().equals("payment_intent.succeeded")) {
        PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
        handlePaymentIntentSucceeded(paymentIntent);
      }

      if (event.getType().equals("account.updated")) {

      }

      if (event.getType().equals("balance.available")) {

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

      if (event.getType().equals("payment_intent.succeeded")) {
        PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
        handlePaymentIntentSucceeded(paymentIntent);
      } else {
        log.warn("Unhandled event type: " + event.getType());
      }

      return JapiResponse.success(null);
    } catch (Exception e) { throw e; }
  }


  private void handlePaymentIntentSucceeded(PaymentIntent paymentIntent) {

  }
}
