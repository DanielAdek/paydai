package com.paydai.api.presentation.dto.invite;

import com.paydai.api.domain.model.AggregateType;

import java.time.LocalDateTime;
import java.util.UUID;

public record InviteRecord(
  UUID inviteId,
  String email,
  String inviteCode,
  Float commission,
  AggregateType aggregate,
  int interval,
  String duration,
  String link,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {
}
