package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.CustomerModel;
import com.paydai.api.domain.repository.CustomerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerRepositoryImpl extends CustomerRepository, JpaRepository<CustomerModel, UUID> {
  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM customer_tbl WHERE id=?1")
  CustomerModel findByCustomerId(UUID customerId);
}
