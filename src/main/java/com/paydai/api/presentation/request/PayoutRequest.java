package com.paydai.api.presentation.request;

import com.stripe.param.PayoutCreateParams;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutRequest {
  private double amount;
  private String currency;
  private UUID workspaceId;
  private String description;
  private String destination;
  private PayoutCreateParams.Method type;
  private PayoutCreateParams.SourceType sourceType;
}
