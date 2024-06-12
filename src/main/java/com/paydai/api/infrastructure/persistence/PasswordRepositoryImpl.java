package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.PasswordModel;
import com.paydai.api.domain.repository.PasswordRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PasswordRepositoryImpl extends PasswordRepository, JpaRepository<PasswordModel, UUID> {
}
