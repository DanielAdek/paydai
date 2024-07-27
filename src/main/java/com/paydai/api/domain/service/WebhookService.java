package com.paydai.api.domain.service;

import com.paydai.api.presentation.dto.webhook.WebhookRegister;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;

public interface WebhookService {
  JapiResponse handleConnectEvents(String payload, Event event);
  JapiResponse handleAccountEvents(String payload, Event event);
  JapiResponse registerWebhook(WebhookRegister webhookRegister);
}
