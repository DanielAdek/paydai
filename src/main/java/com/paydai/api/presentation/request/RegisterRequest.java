package com.paydai.api.presentation.request;

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

  private String fullName;

  private String password;

  private String accountType;

  private String country;

  public static RegisterRequest getRegisterPayload(
    String email, String password, String username,
    String accountType, String country
  ) {
    return new RegisterRequest(email, password, username, accountType, country);
  }
}
