package com.paydai.api.presentation.controller;

import com.paydai.api.presentation.response.JapiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface PayoutLedgerController {
  ResponseEntity<JapiResponse> getPayoutLedgerTransactions(@RequestParam UUID userId, @RequestParam UUID workspaceId);
  ResponseEntity<JapiResponse> getPayoutLedgerTransactions(@RequestParam UUID userId);
  ResponseEntity<JapiResponse> getPayoutTransactionOverview(@RequestParam UUID userId, @RequestParam UUID workspaceId);
  ResponseEntity<JapiResponse> getPayoutTransactionOverview(@RequestParam UUID userId);

}
