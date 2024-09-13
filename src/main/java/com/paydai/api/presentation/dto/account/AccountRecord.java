package com.paydai.api.presentation.dto.account;

import java.util.UUID;

public record AccountRecord(
  double balance,
  UUID user
) {
}
