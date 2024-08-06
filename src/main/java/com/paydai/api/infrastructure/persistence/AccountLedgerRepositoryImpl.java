package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.AccountLedgerModel;
import com.paydai.api.domain.repository.AccountLedgerRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountLedgerRepositoryImpl extends AccountLedgerRepository, JpaRepository<AccountLedgerModel, UUID> {
}
