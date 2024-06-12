package com.paydai.api.presentation.controller;

import com.paydai.api.presentation.request.InviteRequest;
import com.paydai.api.presentation.request.WorkspaceRequest;
import com.paydai.api.presentation.response.JapiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface WorkspaceController {
  ResponseEntity<JapiResponse> inviteToWorkspace(@RequestBody InviteRequest payload);
  ResponseEntity<JapiResponse> create(@RequestBody WorkspaceRequest payload);
}
