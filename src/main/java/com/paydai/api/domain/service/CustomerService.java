package com.paydai.api.domain.service;

import com.paydai.api.domain.model.CustomerModel;
import com.paydai.api.presentation.request.CustomerRequest;
import com.paydai.api.presentation.response.JapiResponse;

public interface CustomerService {
  JapiResponse create(CustomerRequest buildCustomer);
  JapiResponse getCustomers();
}
