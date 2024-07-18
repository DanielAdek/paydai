package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.InvoiceModel;

import java.util.List;
import java.util.UUID;

public interface InvoiceRepository {
  List<InvoiceModel> findByCustomerId(UUID customerId);
  InvoiceModel save(InvoiceModel buildInvoice);
  List<InvoiceModel> findByWorkspaceInvoices(UUID workspaceId);
}
