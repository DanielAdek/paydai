package com.paydai.api.domain.service;

import com.paydai.api.domain.model.InvoiceStatus;
import com.paydai.api.presentation.request.InvoiceRequest;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;

import java.util.List;
import java.util.UUID;

public interface InvoiceService {
  JapiResponse create(InvoiceRequest payload) throws StripeException;
  JapiResponse getInvoiceToCustomer(UUID customerId);
  JapiResponse getWorkspaceInvoicesToCustomers(UUID workspaceId);
  JapiResponse getInvoice(String invoiceCode);
  JapiResponse finalizeInvoice(String invoiceCode) throws StripeException;
  JapiResponse sendInvoice(String invoice) throws StripeException;
  JapiResponse filterInvoice(UUID workspaceId, List<InvoiceStatus> status);
  JapiResponse getSalesRepInvoice(String invoiceCode);
  JapiResponse cancelInvoice(String invoiceCode);
}
