package com.paydai.api.application;

import com.paydai.api.domain.exception.ConflictException;
import com.paydai.api.domain.model.CustomerModel;
import com.paydai.api.domain.model.CustomerType;
import com.paydai.api.domain.repository.CustomerRepository;
import com.paydai.api.domain.service.CustomerService;
import com.paydai.api.presentation.request.CustomerRequest;
import com.paydai.api.presentation.response.JapiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
  private final CustomerRepository repository;

  @Override
  public JapiResponse create(CustomerRequest request) {
    try {
      CustomerModel customerModel = repository.findByCustomerEmail(request.getEmail());

      if (customerModel != null) throw new ConflictException("Customer already exit");

      CustomerModel lead = repository.save(CustomerModel.builder()
        .email(request.getEmail())
        .name(request.getName())
        .stage(CustomerType.LEAD)
        .description(request.getDescription())
        .build()
      );
      return JapiResponse.success(lead);
    } catch (Exception e) { throw e; }
  }

  @Override
  public JapiResponse getCustomers() {
    try {
      List<CustomerModel> customers = repository.findCustomers();
      return JapiResponse.success(customers);
    } catch (Exception e) { throw e; }
  }
}
