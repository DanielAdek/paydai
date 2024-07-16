package com.paydai.api.presentation.dto.auth;

import com.paydai.api.domain.model.EmailModel;
import com.paydai.api.domain.model.EmailType;
import com.paydai.api.domain.model.UserModel;
import com.paydai.api.domain.model.UserType;
import com.paydai.api.presentation.dto.role.RoleDtoMapper;
import com.paydai.api.presentation.dto.role.RoleRecord;
import com.paydai.api.presentation.dto.workspace.WorkspaceRecord;
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
  private UUID id;
  private UserType userType;
  private String email;
  private EmailType emailType;
  private String token;
  private String stripeId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private RoleRecord role;
  private WorkspaceRecord workspace;

  public static AuthModelDto getAuthData(UserModel user, EmailModel email, String token, RoleRecord role, WorkspaceRecord workspace) {
    return new AuthModelDto(
      user.getId(),
      user.getUserType(),
      email.getEmail(),
      email.getEmailType(),
      token,
      user.getStripeId(),
      user.getCreatedAt(),
      user.getUpdatedAt(),
      role,
      workspace
    );
  }
}
