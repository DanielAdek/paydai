package com.paydai.api.domain.service;

import com.paydai.api.presentation.request.InviteRequest;
import com.paydai.api.presentation.response.JapiResponse;

public interface InviteService {
  JapiResponse createInvite(InviteRequest payload);
}
