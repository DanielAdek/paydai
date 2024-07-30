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
  private UUID creatorId;
  private String creator;
  private String creatorRole;

  public static CustomerDto getCustomerDto(CustomerModel customerModel) {
    UUID closerId = customerModel.getCloser() != null ? customerModel.getCloser().getId() : null;
    String closer = customerModel.getCloser() != null ? customerModel.getCloser().getFirstName() + " " + customerModel.getCloser().getLastName() : null;
    UUID creatorId = customerModel.getCreator() != null ? customerModel.getCreator().getId() : null;
    String creator = customerModel.getCreator() != null ? customerModel.getCreator().getFirstName() + " " + customerModel.getCreator().getLastName() : null;
    String role = customerModel.getCreatorRole() != null ? customerModel.getCreatorRole().getRole() : "";

    return new CustomerDto(
      customerModel.getId(),
      customerModel.getName(),
      customerModel.getEmail(),
      customerModel.getPhone(),
      customerModel.getStage(),
      customerModel.getDescription(),
      closerId,
      closer,
      creatorId,
      creator,
      role
    );
  }
}
