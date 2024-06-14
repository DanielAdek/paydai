package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.StripeAccountModel;
import com.paydai.api.domain.repository.StripeAccountRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StripeAccountRepositoryImpl extends StripeAccountRepository, JpaRepository<StripeAccountModel, UUID> {
  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM stripe_account_tbl WHERE user_id=?1")
  StripeAccountModel findByUser(UUID userId);
}
