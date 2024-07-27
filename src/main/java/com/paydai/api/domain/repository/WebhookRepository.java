package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.WebhookModel;

public interface WebhookRepository {
  WebhookModel save(WebhookModel buildWebhook);
}
