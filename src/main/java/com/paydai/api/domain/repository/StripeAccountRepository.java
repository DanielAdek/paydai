package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.StripeAccountModel;

import java.util.UUID;

public interface StripeAccountRepository {
  StripeAccountModel save(StripeAccountModel buildStripe);
  StripeAccountModel findByUser(UUID userId);
}
