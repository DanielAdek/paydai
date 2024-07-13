package com.paydai.api.presentation.dto.account;

import java.util.UUID;

public record StripeAccountRecord(
  UUID userId,
  String stripeId
) {
}
