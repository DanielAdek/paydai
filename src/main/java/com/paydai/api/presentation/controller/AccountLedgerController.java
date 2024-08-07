package com.paydai.api.presentation.controller;

import com.paydai.api.presentation.response.JapiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface AccountLedgerController {
  ResponseEntity<JapiResponse> getUserAccountLedger(@RequestParam UUID userId, @RequestParam UUID workspaceId);
  ResponseEntity<JapiResponse> getUserAccountsLedger(@RequestParam UUID userId);
}
