package com.paydai.api.presentation.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceRequest {
  private UUID customerId;
  private String subject;
  private LocalDateTime dueDate;
  private String currency;

  // product
  private String productName;
  private String productDescription;

  // price
  private Long unitAmount;
}
