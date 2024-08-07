package com.paydai.api.domain.service;

import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;

public interface PayoutLedgerService {
  JapiResponse transferToSalesRep(String stripeInvoiceCode) throws StripeException;
}
