package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.AccountModel;
import com.paydai.api.domain.repository.AccountRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountRepositoryImpl extends AccountRepository, JpaRepository<AccountModel, UUID> { }
