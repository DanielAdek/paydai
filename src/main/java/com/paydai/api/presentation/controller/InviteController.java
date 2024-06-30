package com.paydai.api.presentation.controller;

import com.paydai.api.presentation.request.InviteRequest;
import com.paydai.api.presentation.request.RegisterRequest;
import com.paydai.api.presentation.response.JapiResponse;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface InviteController {
  ResponseEntity<JapiResponse> sendInvite(@RequestBody InviteRequest request) throws MessagingException;

  ResponseEntity<JapiResponse> acceptInvite(@RequestBody RegisterRequest request, @RequestParam String inviteCode);
}
