package com.paydai.api.presentation.dto.userWorkspace;

import java.util.UUID;

public record TeamsRecord(
  UUID userId,
  String firstName,
  String lastName,
  String email,
  UUID roleId,
  String role,
  UUID workspaceId,
  String workspace,
  UUID commSettingId,
  String payoutSchedule,
  Float commission,
  UUID userWorkspaceId
) {
}
