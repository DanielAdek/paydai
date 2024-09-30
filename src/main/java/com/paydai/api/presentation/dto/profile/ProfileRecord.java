package com.paydai.api.presentation.dto.profile;

import com.paydai.api.domain.model.EmailModel;
import com.paydai.api.domain.model.WorkspaceModel;

import java.util.List;
import java.util.UUID;

public record ProfileRecord(
  UUID userId,
  String firstName,
  String lastName
) {
}
