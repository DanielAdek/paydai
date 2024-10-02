package com.paydai.api.presentation.dto.email;

import com.paydai.api.domain.model.EmailType;

import java.time.LocalDateTime;
import java.util.UUID;

public record EmailRecord(
  UUID emailId,
  String email,
  EmailType emailType,
  LocalDateTime joined
) {
}
