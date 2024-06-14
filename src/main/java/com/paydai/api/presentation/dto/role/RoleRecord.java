package com.paydai.api.presentation.dto.role;

import java.time.LocalDateTime;
import java.util.UUID;

public record RoleRecord(
  UUID roleId,
  String role,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {
}
