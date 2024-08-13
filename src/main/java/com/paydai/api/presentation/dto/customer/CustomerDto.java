package com.paydai.api.presentation.dto.customer;

import com.paydai.api.domain.model.*;
import com.paydai.api.presentation.dto.profile.ProfileRecord;
import com.paydai.api.presentation.dto.role.RoleRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
  private UUID id;
  private String name;
  private String email;
  private String phone;
  private CustomerType stage;
  private String description;
  private UUID closerId;
  private String closer;
  private UUID setterId;
  private String setter;

  public static CustomerDto getCustomerDto(CustomerModel customerModel) {
    UUID closerId = customerModel.getCloser() != null ? customerModel.getCloser().getId() : null;
    String closer = customerModel.getCloser() != null ? customerModel.getCloser().getFirstName() + " " + customerModel.getCloser().getLastName() : null;
    UUID setterId = customerModel.getSetter() != null ? customerModel.getSetter().getId() : null;
    String setter = customerModel.getSetter() != null ? customerModel.getSetter().getFirstName() + " " + customerModel.getSetter().getLastName() : null;

    return new CustomerDto(
      customerModel.getId(),
      customerModel.getName(),
      customerModel.getEmail(),
      customerModel.getPhone(),
      customerModel.getStage(),
      customerModel.getDescription(),
      closerId,
      closer,
      setterId,
      setter
    );
  }
}
