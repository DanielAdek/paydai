package com.paydai.api.domain.service;

import com.paydai.api.presentation.request.PermissionRequest;
import com.paydai.api.presentation.response.JapiResponse;

public interface PermissionService {
  JapiResponse getPermissions();

  JapiResponse create(PermissionRequest payload);
}
