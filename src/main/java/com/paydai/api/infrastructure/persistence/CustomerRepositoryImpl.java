package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.CustomerModel;
import com.paydai.api.domain.repository.CustomerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerRepositoryImpl extends CustomerRepository, JpaRepository<CustomerModel, UUID> {
  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM customer_tbl WHERE id=?1")
  CustomerModel findByCustomerId(UUID customerId);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM customer_tbl WHERE email=?1 AND workspace_id=?2")
  CustomerModel findByCustomerEmail(String email, UUID workspaceId);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM customer_tbl WHERE workspace_id=?1")
  List<CustomerModel> findCustomers(UUID workspaceId);
}
