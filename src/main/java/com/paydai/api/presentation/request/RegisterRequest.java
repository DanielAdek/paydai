package com.paydai.api.presentation.request;

import com.paydai.api.domain.model.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
  private String email;
  private String business;
  private String firstName;
  private String lastName;
  private String password;
  private UserType userType;
  private CountryRequest country;
}
