package com.paydai.api.presentation.dto.profile;

import java.util.UUID;

public record ProfileRecord(
  UUID userId,
  String firstName,
  String lastName
) {
}
