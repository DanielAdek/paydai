package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.PayoutTxnModel;
import com.paydai.api.domain.repository.PayoutTxnRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PayoutTxnRepositoryImpl extends PayoutTxnRepository, JpaRepository<PayoutTxnModel, UUID> {
  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM payout_txn_tbl WHERE user_id=?1")
  List<PayoutTxnModel> findUserPayouts(UUID userId);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM payout_txn_tbl WHERE user_id=?1 AND workspace_id=?2")
  List<PayoutTxnModel> findUserWorkspacePayouts(UUID userId, UUID workspaceId);
}
