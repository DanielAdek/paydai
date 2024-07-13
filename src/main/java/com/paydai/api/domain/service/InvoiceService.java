package com.paydai.api.domain.service;

import com.paydai.api.presentation.request.InvoiceRequest;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;

import java.util.UUID;

public interface InvoiceService {
  JapiResponse create(InvoiceRequest payload) throws StripeException;
  JapiResponse getInvoiceToCustomer(UUID customerId);
}
