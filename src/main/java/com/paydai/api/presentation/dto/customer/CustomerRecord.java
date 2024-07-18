package com.paydai.api.presentation.dto.customer;

import com.paydai.api.domain.model.CustomerType;

import java.util.UUID;

public record CustomerRecord (
  UUID id,
  String name,
  String email,
  String phone,
  CustomerType stage,
  String description,
  String closer,
  String creator,
  String creatorRole
) {
}
