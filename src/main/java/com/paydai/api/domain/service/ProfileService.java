package com.paydai.api.domain.service;

import com.paydai.api.domain.model.UserModel;
import com.paydai.api.presentation.request.UpdateProfileRequest;
import com.paydai.api.presentation.response.JapiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface ProfileService {
  JapiResponse switchWorkspaceProfile(UUID workspaceId);
  UserModel getLoggedInUser();
  JapiResponse updateProfile(UpdateProfileRequest profileRequest);
  JapiResponse getUserProfile();
}
