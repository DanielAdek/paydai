package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.InvoiceModel;
import com.paydai.api.domain.repository.InvoiceRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InvoiceRepositoryImpl extends InvoiceRepository, JpaRepository<InvoiceModel, UUID> {
  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM invoice_tbl WHERE customer_id=?1")
  List<InvoiceModel> findByCustomerId(UUID customerId);
}
