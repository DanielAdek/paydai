package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.AccountLedgerModel;
import com.paydai.api.domain.repository.AccountLedgerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface AccountLedgerRepositoryImpl extends AccountLedgerRepository, JpaRepository<AccountLedgerModel, UUID> {
  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM account_ledger_tbl WHERE user_id=?1")
  AccountLedgerModel findAccountLedgerByUser(UUID userId);
}
