package com.paydai.api.presentation.dto.auth;

import com.paydai.api.domain.model.EmailType;
import com.paydai.api.domain.model.UserType;
import com.paydai.api.presentation.dto.role.RoleRecord;
import com.paydai.api.presentation.dto.workspace.WorkspaceRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AuthRecordDto (
  UUID userId,
  String firstName,
  String lastName,
  UserType userType,
  String email,
  EmailType emailType,
  String stripeId,
  String token,
  LocalDateTime createdAt,
  LocalDateTime updatedAt,
  RoleRecord role,
  WorkspaceRecord workspace
) {

}