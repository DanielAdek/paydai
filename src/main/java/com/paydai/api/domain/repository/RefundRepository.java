package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.InvoiceModel;
import com.paydai.api.domain.model.RefundModel;

import java.util.List;
import java.util.UUID;

public interface RefundRepository {
  RefundModel save(RefundModel buildRefund);
  List<RefundModel> findRefundRequests(UUID userId, UUID workspaceId);
  List<RefundModel> findRefundsRequests(UUID workspaceId);
  List<RefundModel> findSalesRepLiabilities(UUID userId, UUID workspaceId);
  Double findLiabilities(UUID userId, UUID workspaceId);
  Double findTotalLiabilities(UUID userId);
  Double findTotalRefundPaid(UUID userId, UUID workspaceId, UUID invoiceId);
}
