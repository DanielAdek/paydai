package com.paydai.api.presentation.controller;

import com.paydai.api.presentation.request.AuthRequest;
import com.paydai.api.presentation.request.RegisterRequest;
import com.paydai.api.presentation.response.JapiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthController {
  ResponseEntity<JapiResponse> create(@RequestBody RegisterRequest payload);
  ResponseEntity<JapiResponse> authenticate(@RequestBody AuthRequest payload);
}
