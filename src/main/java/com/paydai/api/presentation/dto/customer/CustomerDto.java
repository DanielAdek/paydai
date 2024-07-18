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
  private String closer;
  private String creator;
  private String creatorRole;

  public static CustomerDto getCustomerDto(CustomerModel customerModel) {
    String closer = customerModel.getCloser() != null ? customerModel.getCloser().getFirstName() + " " + customerModel.getCloser().getLastName() : "";
    String creator = customerModel.getCreator() != null ? customerModel.getCreator().getFirstName() + " " + customerModel.getCreator().getLastName() : "";
    String role = customerModel.getCreatorRole() != null ? customerModel.getCreatorRole().getRole() : "";

    return new CustomerDto(
      customerModel.getId(),
      customerModel.getName(),
      customerModel.getEmail(),
      customerModel.getPhone(),
      customerModel.getStage(),
      customerModel.getDescription(),
      closer,
      creator,
      role
    );
  }
}
