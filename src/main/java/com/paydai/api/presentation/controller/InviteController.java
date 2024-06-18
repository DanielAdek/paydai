package com.paydai.api.presentation.controller;

import com.paydai.api.presentation.request.InviteRequest;
import com.paydai.api.presentation.response.JapiResponse;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface InviteController {
  ResponseEntity<JapiResponse> sendInvite(@RequestBody InviteRequest request) throws MessagingException;
}
