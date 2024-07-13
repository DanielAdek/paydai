package com.paydai.api.presentation.controller;

import com.paydai.api.presentation.request.RoleRequest;
import com.paydai.api.presentation.response.JapiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface RoleController {
  ResponseEntity<JapiResponse> getAllRoles();
  ResponseEntity<JapiResponse> create(@RequestBody RoleRequest payload);
}
