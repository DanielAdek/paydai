package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.TransactionModel;
import com.paydai.api.domain.repository.TransactionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepositoryImpl extends TransactionRepository, JpaRepository<TransactionModel, UUID> {
  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM transaction_ledger_tbl WHERE stripe_invoice_code=?1")
  TransactionModel findTransactionByStripeInvoiceId(String invoiceCode);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM transaction_ledger_tbl WHERE user_id=?1")
  List<TransactionModel> findTransactions(UUID userId);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM transaction_ledger_tbl WHERE user_id=?1 AND workspace_id=?2")
  List<TransactionModel> findTransactions(UUID userId, UUID workspaceId);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM transaction_ledger_tbl WHERE workspace_id=?1")
  List<TransactionModel> findTransactionsMerchant(UUID workspaceId);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM transaction_ledger_tbl WHERE user_id=?1 AND workspace_id=?2 AND DATE(payout_date) = CURRENT_DATE")
  List<TransactionModel> findTransactionsForToday(UUID userId, UUID workspaceId);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM transaction_ledger_tbl WHERE user_id=?1 AND workspace_id=?2 AND DATE_TRUNC('week', payout_date) = DATE_TRUNC('week', CURRENT_DATE)")
  List<TransactionModel> findTransactionsForCurrentWeek(UUID userId, UUID workspaceId);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM transaction_ledger_tbl WHERE user_id=?1 AND workspace_id=?2 AND DATE_TRUNC('month', payout_date) = DATE_TRUNC('month', CURRENT_DATE)")
  List<TransactionModel> findTransactionsForCurrentMonth(UUID userId, UUID workspaceId);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM transaction_ledger_tbl WHERE user_id=?1 AND workspace_id=?2 AND DATE_TRUNC('year', payout_date) = DATE_TRUNC('year', CURRENT_DATE)")
  List<TransactionModel> findTransactionsForCurrentYear(UUID userId, UUID workspaceId);
}
