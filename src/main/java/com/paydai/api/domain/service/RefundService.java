package com.paydai.api.domain.service;

import com.paydai.api.presentation.request.RefundRequest;
import com.paydai.api.presentation.response.JapiResponse;

import java.util.UUID;

public interface RefundService {
  JapiResponse create(RefundRequest payload);
  JapiResponse getRefundRequests(UUID workspaceId);
  JapiResponse getRefundsRequests(UUID workspaceId);
  JapiResponse getLiabilityBalance(UUID workspaceId);
  JapiResponse getLiabilityBalance();
}
