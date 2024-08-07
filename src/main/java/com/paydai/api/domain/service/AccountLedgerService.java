package com.paydai.api.domain.service;

import com.paydai.api.presentation.response.JapiResponse;

import java.util.UUID;

public interface AccountLedgerService {
  JapiResponse getUserAccountLedger(UUID userId, UUID workspaceId);
  JapiResponse getUserAccountsLedger(UUID uuid);
}
