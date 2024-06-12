package com.paydai.api.presentation.dto.auth;

import com.paydai.api.domain.model.EmailModel;
import com.paydai.api.domain.model.EmailType;
import com.paydai.api.domain.model.UserModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthModelDto {
  private UUID userId;
  private String userType;
  private String email;
  private EmailType emailType;
  private String token;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static AuthModelDto getAuthData(UserModel user, EmailModel email, String token) {
    return new AuthModelDto(
      user.getUserId(),
      user.getUserType(),
      email.getEmail(),
      email.getEmailType(),
      token,
      user.getCreatedAt(),
      user.getUpdatedAt()
    );
  }
}
