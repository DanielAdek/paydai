package com.paydai.api.presentation.controller;

import com.paydai.api.presentation.request.PermissionRequest;
import com.paydai.api.presentation.response.JapiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface PermissionController {
  ResponseEntity<JapiResponse> getPermission();

  ResponseEntity<JapiResponse> create(@RequestBody PermissionRequest payload);
}
