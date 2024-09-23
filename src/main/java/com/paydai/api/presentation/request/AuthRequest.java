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
public class AuthRequest {
  private String email;
  private String password;
  private UserType loginType;

  public static AuthRequest getAuthCred(String email, String password, UserType loginType) {
    return new AuthRequest(email, password, loginType);
  }
}