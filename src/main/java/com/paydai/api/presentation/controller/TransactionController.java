package com.paydai.api.presentation.controller;

import com.paydai.api.presentation.request.TransferRequest;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface TransactionController {
  ResponseEntity<JapiResponse> getPayoutLedgerTransactions(@RequestParam UUID userId, @RequestParam UUID workspaceId);
  ResponseEntity<JapiResponse> getPayoutLedgerTransactions(@RequestParam UUID userId);
  ResponseEntity<JapiResponse> getPayoutTransactionOverview(@RequestParam UUID userId, @RequestParam UUID workspaceId);
  ResponseEntity<JapiResponse> getPayoutTransactionOverview(@RequestParam UUID userId);
  ResponseEntity<JapiResponse> directTransferToSalesRep(@RequestBody TransferRequest payload) throws StripeException;
  ResponseEntity<JapiResponse> getTransactionsMerchant(@RequestParam UUID workspaceId);
}
