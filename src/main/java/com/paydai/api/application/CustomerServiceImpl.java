package com.paydai.api.application;

import com.paydai.api.domain.annotation.TryCatchException;
import com.paydai.api.domain.exception.ConflictException;
import com.paydai.api.domain.model.*;
import com.paydai.api.domain.repository.CustomerRepository;
import com.paydai.api.domain.service.CustomerService;
import com.paydai.api.presentation.dto.customer.CustomerDto;
import com.paydai.api.presentation.dto.customer.CustomerDtoMapper;
import com.paydai.api.presentation.dto.customer.CustomerRecord;
import com.paydai.api.presentation.request.CustomerRequest;
import com.paydai.api.presentation.response.JapiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
  private final CustomerRepository repository;
  private final CustomerDtoMapper customerDtoMapper;

  @Override
  @TryCatchException
  public JapiResponse create(CustomerRequest request) {
    CustomerModel customerModel = repository.findByCustomerEmail(request.getEmail(), request.getWorkspaceId());

    if (customerModel != null) throw new ConflictException("Customer already exit");

    CustomerModel newCustomer = CustomerModel.builder()
      .name(request.getName())
      .stage(CustomerType.LEAD)
      .email(request.getEmail())
      .phone(request.getPhone())
      .description(request.getDescription())
      .closer(UserModel.builder().id(request.getCloserId()).build())
      .workspace(WorkspaceModel.builder().id(request.getWorkspaceId()).build())
      .build();

    if (request.getCreatorRole().equals("setter")) {
      newCustomer.setSetterInvolved(true);
      newCustomer.setSetter(UserModel.builder().id(request.getSetterId()).build());
    }

    CustomerModel lead = repository.save(newCustomer);

    CustomerDto buildCustomerDto = CustomerDto.getCustomerDto(lead);

    CustomerRecord customerRecord = customerDtoMapper.apply(buildCustomerDto);

    return JapiResponse.success(customerRecord);
  }

  @Override
  @TryCatchException
  public JapiResponse getCustomers(UUID workspaceId) {
    List<CustomerModel> customers = repository.findCustomers(workspaceId);

    List<CustomerRecord> customerRecords = customers
      .stream()
      .map(customerModel -> customerDtoMapper.apply(
        CustomerDto.getCustomerDto(customerModel)
      ))
      .collect(Collectors.toList());

    return JapiResponse.success(customerRecords);
  }

  @Override
  @TryCatchException
  public JapiResponse getOneCustomer(UUID customerId) {
    CustomerModel customerModel = repository.findByCustomerId(customerId);
    return JapiResponse.success(customerDtoMapper.apply(CustomerDto.getCustomerDto(customerModel)));
  }
}
