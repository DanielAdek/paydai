package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.RefundModel;
import com.paydai.api.domain.repository.RefundRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface RefundRepositoryImpl extends RefundRepository, JpaRepository<RefundModel, UUID> {
  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM refund_tbl WHERE user_id=?1 AND workspace_id=?2")
  List<RefundModel> findRefundRequests(UUID userId, UUID workspaceId);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM refund_tbl WHERE workspace_id=?1")
  List<RefundModel> findRefundsRequests(UUID workspaceId);

  @Override
  @Query(nativeQuery = true, value = "SELECT SUM(amount - total_paid)  FROM refund_tbl WHERE user_id=?1 AND workspace_id=?2 AND status <> 'PAID'")
  Double findLiabilities(UUID userId, UUID workspaceId);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM refund_tbl WHERE user_id=?1 AND workspace_id=?2 AND status <> 'PAID' ORDER BY created_at ASC")
  List<RefundModel> findSalesRepLiabilities(UUID userId, UUID workspaceId);

  @Override
  @Query(nativeQuery = true, value = "SELECT SUM(amount - total_paid) FROM refund_tbl WHERE user_id=?1 AND status <> 'PAID'")
  Double findTotalLiabilities(UUID userId);

  @Override
  @Query(nativeQuery = true, value = "SELECT SUM(amount) FROM refund_tbl WHERE user_id=?1 AND workspace_id=?2 AND invoice_id=?3")
  Double findTotalRefundPaid(UUID userId, UUID workspaceId, UUID invoiceId);
}
