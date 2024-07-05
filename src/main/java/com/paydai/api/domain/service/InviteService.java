package com.paydai.api.domain.service;

import com.paydai.api.presentation.request.InviteRequest;
import com.paydai.api.presentation.request.RegisterRequest;
import com.paydai.api.presentation.response.JapiResponse;
import jakarta.mail.MessagingException;

public interface InviteService {
  JapiResponse createInvite(InviteRequest payload) throws MessagingException;
  JapiResponse acceptInvite(RegisterRequest request, String inviteCode);
}
