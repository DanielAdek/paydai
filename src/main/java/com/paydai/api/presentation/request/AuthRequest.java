package com.paydai.api.presentation.request;

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
  private String loginType;

  public static AuthRequest getAuthCred(String email, String password, String loginType) {
    return new AuthRequest(email, password, loginType);
  }
}