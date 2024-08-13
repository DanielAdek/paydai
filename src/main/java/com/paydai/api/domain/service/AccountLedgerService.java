package com.paydai.api.domain.service;

import com.paydai.api.domain.model.AccountLedgerModel;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;

import java.util.UUID;

public interface AccountLedgerService {
  JapiResponse getUserAccountLedger(UUID userId) throws StripeException;
  AccountLedgerModel updateSalesRepAccountLedgerBalance(UUID userId) throws StripeException;
}
