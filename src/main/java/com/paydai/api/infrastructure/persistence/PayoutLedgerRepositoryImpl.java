package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.PayoutLedgerModel;
import com.paydai.api.domain.repository.PayoutLedgerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PayoutLedgerRepositoryImpl extends PayoutLedgerRepository, JpaRepository<PayoutLedgerModel, UUID> {

}
