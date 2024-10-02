package com.paydai.api.presentation.dto.userWorkspace;

import com.paydai.api.presentation.dto.email.EmailRecord;
import com.paydai.api.presentation.dto.profile.ProfileRecord;
import com.paydai.api.presentation.dto.role.RoleRecord;
import com.paydai.api.presentation.dto.workspace.WorkspaceRecord;

import java.util.UUID;

public record UserWorkspaceRecord(
  ProfileRecord user,
  WorkspaceRecord workspace,
  RoleRecord role,
  EmailRecord email
) {
}
