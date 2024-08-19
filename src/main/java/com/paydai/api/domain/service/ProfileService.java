package com.paydai.api.domain.service;

import com.paydai.api.presentation.response.JapiResponse;

import java.util.UUID;

public interface ProfileService {
  JapiResponse switchWorkspaceProfile(UUID workspaceId);
}
