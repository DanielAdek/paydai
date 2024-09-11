package com.paydai.api.domain.service;

import com.paydai.api.presentation.request.AuthRequest;
import com.paydai.api.presentation.request.OauthRequest;
import com.paydai.api.presentation.request.RegisterRequest;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;

public interface AuthService {
  JapiResponse create(RegisterRequest payload);
  JapiResponse authenticate(AuthRequest payload) throws StripeException;
}
