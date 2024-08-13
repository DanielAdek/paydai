package com.paydai.api.presentation.dto.customer;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class CustomerDtoMapper implements Function<CustomerDto, CustomerRecord> {

  @Override
  public CustomerRecord apply(CustomerDto customerDto) {
    return new CustomerRecord(
      customerDto.getId(),
      customerDto.getName(),
      customerDto.getEmail(),
      customerDto.getPhone(),
      customerDto.getStage(),
      customerDto.getDescription(),
      customerDto.getCloserId(),
      customerDto.getCloser(),
      customerDto.getSetterId(),
      customerDto.getSetter()
    );
  }
}
