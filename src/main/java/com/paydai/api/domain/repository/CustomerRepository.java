package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.CustomerModel;

import java.util.UUID;

public interface CustomerRepository {
  CustomerModel save(CustomerModel buildCustomer);
  CustomerModel findByCustomerId(UUID customerId);
}
