package com.paydai.api.presentation.controller;

import com.paydai.api.presentation.response.JapiResponse;
import org.springframework.http.ResponseEntity;

public interface AuthController {
  ResponseEntity<JapiResponse> create();
}

