package com.paydai.api.domain.service;

import com.paydai.api.presentation.request.WorkspaceRequest;
import com.paydai.api.presentation.response.JapiResponse;

import java.util.UUID;

public interface WorkspaceService {
  JapiResponse getWorkspace();
  JapiResponse createWorkspace(WorkspaceRequest payload);
  JapiResponse getSalesRepWorkspaces();
  JapiResponse getWorkspaceSalesReps(UUID workspaceId, UUID roleId);
  JapiResponse getWorkspaceTeams(UUID workspaceId);
}
