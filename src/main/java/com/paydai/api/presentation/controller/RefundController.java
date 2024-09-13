package com.paydai.api.presentation.controller;

import com.paydai.api.presentation.request.RefundRequest;
import com.paydai.api.presentation.response.JapiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface RefundController {
  ResponseEntity<JapiResponse> create(@RequestBody RefundRequest payload);
  ResponseEntity<JapiResponse> getRefundRequests(@RequestParam UUID workspaceId);
  ResponseEntity<JapiResponse> getRefundsRequests(@RequestParam UUID workspaceId);
  ResponseEntity<JapiResponse> getLiabilityBalance(@RequestParam UUID workspaceId);
  ResponseEntity<JapiResponse> getTotalLiability();
}
