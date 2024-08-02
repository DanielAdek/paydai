package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.InvoiceModel;
import com.paydai.api.domain.model.InvoiceStatus;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface InvoiceRepository {
  List<InvoiceModel> findByCustomerId(UUID customerId);
  InvoiceModel save(InvoiceModel buildInvoice);
  List<InvoiceModel> findByWorkspaceInvoices(UUID workspaceId);
  InvoiceModel findByInvoiceCode(String code);
  void updateInvoiceByInvoiceCode(
    String invoiceCode,
    double commission,
    String stripeInvoiceDetails,
    String stripeInvoiceHostUrl,
    String stripeInvoicePdf,
    String stripeInvoiceStatus,
    String status
  );
  void updateInvoiceStatus(String invoiceCode, String stripeInvoiceStatus);
  void updateInvoiceStatusWebhook(String invoiceCode, String stripeInvoiceStatus, InvoiceStatus status);
}
