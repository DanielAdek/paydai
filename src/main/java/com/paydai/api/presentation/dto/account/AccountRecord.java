package com.paydai.api.presentation.dto.account;

import java.util.UUID;

public record AccountRecord(
  double balance,
  double pendBal,
  String currency,
  UUID user
) {
}
