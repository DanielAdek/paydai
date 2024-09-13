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
public class RefundRequest {
  private String reason;
  private double amount;
  private boolean full;
  private UUID salesRepId;
  private UUID workspaceId;
  private String invoiceCode;
}
