package com.paydai.api.application;

import com.paydai.api.domain.exception.ConflictException;
import com.paydai.api.domain.model.CustomerModel;
import com.paydai.api.domain.model.CustomerType;
import com.paydai.api.domain.model.RoleModel;
import com.paydai.api.domain.model.UserModel;
import com.paydai.api.domain.repository.CustomerRepository;
import com.paydai.api.domain.service.CustomerService;
import com.paydai.api.presentation.dto.customer.CustomerDto;
import com.paydai.api.presentation.dto.customer.CustomerDtoMapper;
import com.paydai.api.presentation.dto.customer.CustomerRecord;
import com.paydai.api.presentation.dto.profile.ProfileDtoMapper;
import com.paydai.api.presentation.dto.profile.ProfileRecord;
import com.paydai.api.presentation.dto.role.RoleDtoMapper;
import com.paydai.api.presentation.request.CustomerRequest;
import com.paydai.api.presentation.response.JapiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
  private final RoleDtoMapper roleDtoMapper;
  private final CustomerRepository repository;
  private final ProfileDtoMapper profileDtoMapper;
  private final CustomerDtoMapper customerDtoMapper;

  @Override
  public JapiResponse create(CustomerRequest request) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      UserModel userModel = (UserModel) authentication.getPrincipal();

      CustomerModel customerModel = repository.findByCustomerEmail(request.getEmail());

      if (customerModel != null) throw new ConflictException("Customer already exit");

      CustomerModel newCustomer = CustomerModel.builder()
        .creator(userModel)
        .name(request.getName())
        .stage(CustomerType.LEAD)
        .email(request.getEmail())
        .phone(request.getPhone())
        .description(request.getDescription())
        .closer(UserModel.builder().id(request.getCloserId()).build())
        .build();

      if (request.getRoleId() != null) newCustomer.setCreatorRole(RoleModel.builder().id(request.getRoleId()).build());

      CustomerModel lead = repository.save(newCustomer);

      CustomerDto buildCustomerDto = CustomerDto.getCustomerDto(lead);

      CustomerRecord customerRecord = customerDtoMapper.apply(buildCustomerDto);

      return JapiResponse.success(customerRecord);
    } catch (Exception e) { throw e; }
  }

  @Override
  public JapiResponse getCustomers() {
    try {
      List<CustomerModel> customers = repository.findCustomers();

      List<CustomerRecord> customerRecords = customers
        .stream()
        .map(customerModel -> customerDtoMapper.apply(
          CustomerDto.getCustomerDto(customerModel)
        ))
        .collect(Collectors.toList());

      return JapiResponse.success(customerRecords);
    } catch (Exception e) { throw e; }
  }
}
