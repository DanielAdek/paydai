package com.paydai.api.domain.service;

import com.paydai.api.presentation.dto.webhook.WebhookRegister;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;

public interface WebhookService {
  JapiResponse handleInvoiceEventConnectAccount(String payload, Event event) throws StripeException;
  JapiResponse handleTransferEventAccount(String payload, Event event) throws StripeException;
  JapiResponse handleBalanceEventConnectAccount(String payload, Event event);
}
