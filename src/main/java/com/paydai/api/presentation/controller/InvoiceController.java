package com.paydai.api.presentation.controller;

import com.paydai.api.presentation.request.InvoiceRequest;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface InvoiceController {
  ResponseEntity<JapiResponse> create(@RequestBody InvoiceRequest payload) throws StripeException;
  ResponseEntity<JapiResponse> getInvoicesToCustomer(@RequestParam UUID customerId);
  ResponseEntity<JapiResponse> getInvoice(@RequestParam String invoiceCode);
  ResponseEntity<JapiResponse> getWorkspaceInvoicesToCustomers(@RequestParam UUID workspaceId);
  ResponseEntity<JapiResponse> finalizeInvoice(@RequestParam String invoiceCode) throws StripeException;
  ResponseEntity<JapiResponse> sendInvoice(@RequestParam String invoice) throws StripeException;
}
