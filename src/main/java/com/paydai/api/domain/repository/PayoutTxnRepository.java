package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.PayoutTxnModel;

import java.util.List;
import java.util.UUID;

public interface PayoutTxnRepository {
  PayoutTxnModel save(PayoutTxnModel buildPayoutTxn);
  List<PayoutTxnModel> findUserPayouts(UUID userId);
  List<PayoutTxnModel> findUserWorkspacePayouts(UUID userId, UUID workspaceId);
}
