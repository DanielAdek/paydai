package com.paydai.api.presentation.dto.account;

import java.time.LocalDateTime;
import java.util.UUID;

public record StripeAccountRecord(
  UUID stripeAccountId,
  UUID userId,
  String stripeId,
  String personalEmail,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {
}
