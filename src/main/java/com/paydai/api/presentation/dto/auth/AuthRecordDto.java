package com.paydai.api.presentation.dto.auth;

import com.paydai.api.domain.model.EmailType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AuthRecordDto (
  UUID userId,
  String userType,
  String email,
  EmailType emailType,
  String token,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {

}