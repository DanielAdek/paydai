package com.paydai.api.domain.service;

import com.paydai.api.presentation.request.InviteRequest;
import com.paydai.api.presentation.request.RegisterRequest;
import com.paydai.api.presentation.response.JapiResponse;
import jakarta.mail.MessagingException;

import java.util.UUID;

public interface InviteService {
  JapiResponse createInvite(InviteRequest payload) throws MessagingException;
  JapiResponse acceptInvite(RegisterRequest request, String inviteCode);
  JapiResponse getWorkspaceInvites(UUID workspaceId);
  JapiResponse cancelInvite(String inviteCode);
}
