package com.paydai.api.presentation.controller;

import com.paydai.api.presentation.response.JapiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface ProfileController {
  ResponseEntity<JapiResponse> switchWorkspaceProfile(@RequestParam UUID workspaceId);
}
