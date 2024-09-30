package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.InvoiceModel;
import com.paydai.api.domain.model.InvoiceStatus;
import com.paydai.api.domain.repository.InvoiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InvoiceRepositoryImpl extends InvoiceRepository, JpaRepository<InvoiceModel, UUID> {
  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM invoice_tbl WHERE customer_id=?1")
  List<InvoiceModel> findByCustomerId(UUID customerId);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM invoice_tbl WHERE workspace_id=?1")
  List<InvoiceModel> findByWorkspaceInvoices(UUID workspaceId);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM invoice_tbl WHERE invoice_code=?1")
  InvoiceModel findByInvoiceCode(String code);

  @Override
  @Transactional
  @Modifying
  @Query(nativeQuery = true, value = "UPDATE invoice_tbl SET stripe_invoice_details=:stripeInvoiceDetails, stripe_invoice_hosted_url=:stripeInvoiceHostUrl, stripe_invoice_status=:stripeInvoiceStatus, stripe_invoice_pdf=:stripeInvoicePdf, status=:status WHERE invoice_code=:invoiceCode")
  void updateInvoiceByInvoiceCode(
    @Param("invoiceCode") String invoiceCode,
//    @Param("commission") double commission,
    @Param("stripeInvoiceDetails") String stripeInvoiceDetails,
    @Param("stripeInvoiceHostUrl") String stripeInvoiceHostUrl,
    @Param("stripeInvoicePdf") String stripeInvoicePdf,
    @Param("stripeInvoiceStatus") String stripeInvoiceStatus,
    @Param("status") String status
  );

  @Override
  @Transactional
  @Modifying
  @Query(nativeQuery = true, value = "UPDATE invoice_tbl SET stripe_invoice_status=?2, status=?3 WHERE invoice_code=?1 OR stripe_invoice_id=?1")
  void updateInvoiceStatus(String invoiceCode, String stripeInvoiceStatus, String status);

  @Override
  @Transactional
  @Modifying
  @Query(nativeQuery = true, value = "UPDATE invoice_tbl SET stripe_invoice_status=?2, status=?3 WHERE invoice_code=?1")
  void updateInvoiceStatusWebhook(String invoiceCode, String stripeInvoiceStatus, String status);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM invoice_tbl WHERE stripe_invoice_id=?1")
  InvoiceModel findByStripeInvoiceCode(String stripeInvoiceCode);

  @Query(nativeQuery = true, value = "SELECT * FROM invoice_tbl WHERE workspace_id = ?1 AND status IN (?2)")
  List<InvoiceModel> findByFilterWorkspaceInvoices(UUID workspaceId, List<String> status);

}
