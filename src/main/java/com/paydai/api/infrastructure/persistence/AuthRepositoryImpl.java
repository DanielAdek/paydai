package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.AuthModel;
import com.paydai.api.domain.repository.AuthRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthRepositoryImpl extends AuthRepository, JpaRepository<AuthModel, UUID> { }
