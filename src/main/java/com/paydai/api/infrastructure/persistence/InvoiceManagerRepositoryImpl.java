package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.InvoiceManagerModel;
import com.paydai.api.domain.repository.InvoiceManagerRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InvoiceManagerRepositoryImpl extends InvoiceManagerRepository, JpaRepository<InvoiceManagerModel, UUID> {
}
