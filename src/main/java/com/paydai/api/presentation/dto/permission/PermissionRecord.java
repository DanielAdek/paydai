package com.paydai.api.presentation.dto.permission;

import java.time.LocalDateTime;
import java.util.UUID;

public record PermissionRecord(
  UUID permissionId,
  String permission,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {
}
