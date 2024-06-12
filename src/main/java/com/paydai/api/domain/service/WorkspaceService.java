package com.paydai.api.domain.service;

import com.paydai.api.presentation.request.InviteRequest;
import com.paydai.api.presentation.request.PermissionRequest;
import com.paydai.api.presentation.request.WorkspaceRequest;
import com.paydai.api.presentation.response.JapiResponse;

public interface WorkspaceService {
  JapiResponse inviteToWorkspace(InviteRequest payload);

  JapiResponse createWorkspace(WorkspaceRequest payload);
}
