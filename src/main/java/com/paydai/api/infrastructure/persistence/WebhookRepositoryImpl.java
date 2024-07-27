package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.WebhookModel;
import com.paydai.api.domain.repository.WebhookRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WebhookRepositoryImpl extends WebhookRepository, JpaRepository<WebhookModel, UUID> {
}
