package com.paydai.api.presentation.controller;

import com.paydai.api.presentation.request.InviteRequest;
import com.paydai.api.presentation.request.WorkspaceRequest;
import com.paydai.api.presentation.response.JapiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface WorkspaceController {
  ResponseEntity<JapiResponse> getWorkspace();
  ResponseEntity<JapiResponse> getSalesRepWorkspaces();
  ResponseEntity<JapiResponse> getWorkspaceSalesReps(@RequestParam UUID workspaceId, @RequestParam UUID roleId);
  ResponseEntity<JapiResponse> getWorkspaceTeams(@RequestParam UUID workspaceId);
}
