package com.paydai.api.presentation.dto.workspace;

import java.time.LocalDateTime;
import java.util.UUID;

public record WorkspaceRecord(
  UUID permissionId,
  String permission,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {
}
