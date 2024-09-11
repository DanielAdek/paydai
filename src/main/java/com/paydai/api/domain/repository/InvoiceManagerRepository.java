package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.InvoiceManagerModel;

public interface InvoiceManagerRepository {
  InvoiceManagerModel save(InvoiceManagerModel buildInvoiceManager);
}
