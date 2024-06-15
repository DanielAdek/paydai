package com.paydai.api.domain.service;

import com.paydai.api.presentation.request.InviteRequest;
import com.paydai.api.presentation.request.PermissionRequest;
import com.paydai.api.presentation.request.WorkspaceRequest;
import com.paydai.api.presentation.response.JapiResponse;

import java.util.UUID;

public interface WorkspaceService {
  JapiResponse getWorkspace(UUID workspaceId);

  JapiResponse createWorkspace(WorkspaceRequest payload);
}
