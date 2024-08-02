package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.CustomerModel;

import java.util.UUID;
import java.util.List;


public interface CustomerRepository {
  CustomerModel save(CustomerModel buildCustomer);
  CustomerModel findByCustomerId(UUID customerId);
  CustomerModel findByCustomerEmail(String email, UUID workspaceId);
  List<CustomerModel> findCustomers();
}
