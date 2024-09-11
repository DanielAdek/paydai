package com.paydai.api.presentation.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
  private UUID salesRepId;
  private UUID workspaceId;
  private double amount;
  private String currency;
  private String narration;
}
