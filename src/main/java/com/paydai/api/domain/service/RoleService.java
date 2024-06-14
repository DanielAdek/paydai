package com.paydai.api.domain.service;

import com.paydai.api.presentation.request.RoleRequest;
import com.paydai.api.presentation.response.JapiResponse;

import java.util.UUID;

public interface RoleService {
  JapiResponse createRole(RoleRequest payload);

  JapiResponse getRolesByWorkspace(UUID workspaceId);
}
