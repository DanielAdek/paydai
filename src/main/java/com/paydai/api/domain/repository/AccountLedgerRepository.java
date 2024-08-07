package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.AccountLedgerModel;

import java.util.List;
import java.util.UUID;

public interface AccountLedgerRepository {
  AccountLedgerModel save(AccountLedgerModel buildAccountLedger);
  AccountLedgerModel findAccountLedgerByUserWorkspace(UUID userId, UUID workspaceId);
  List<AccountLedgerModel> findAccountLedgerByUser(UUID uuid);
}
