package com.paydai.api.presentation.controller;

import com.paydai.api.presentation.request.UpdateProfileRequest;
import com.paydai.api.presentation.response.JapiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface ProfileController {
  ResponseEntity<JapiResponse> switchWorkspaceProfile(@RequestParam UUID workspaceId);
  ResponseEntity<JapiResponse> getProfileData();
  ResponseEntity<JapiResponse> updateProfileData(@RequestBody UpdateProfileRequest updateRequest);
}
