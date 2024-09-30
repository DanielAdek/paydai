package com.paydai.api.domain.service;

import com.paydai.api.presentation.request.AssignSalesRepRequest;
import com.paydai.api.presentation.request.WorkspaceRequest;
import com.paydai.api.presentation.response.JapiResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceService {
  JapiResponse getWorkspace();
  JapiResponse createWorkspace(WorkspaceRequest payload);
  JapiResponse getSalesRepWorkspaces();
  JapiResponse getWorkspaceSalesReps(UUID workspaceId, Optional<UUID> roleId);
  JapiResponse getWorkspaceTeams(UUID workspaceId);
  JapiResponse getManagerTeamMembers(UUID workspaceId);
  JapiResponse assignTeamMembers(AssignSalesRepRequest assignSalesRepRequest);
  JapiResponse removeWorkspaceMember(UUID userId, UUID workspaceId);
}
