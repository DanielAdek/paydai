package com.paydai.api.domain.service;

import com.paydai.api.presentation.request.AuthRequest;
import com.paydai.api.presentation.request.RegisterRequest;
import com.paydai.api.presentation.response.JapiResponse;

public interface AuthService {
  JapiResponse create(RegisterRequest payload);

  JapiResponse authenticate(AuthRequest payload);
}
