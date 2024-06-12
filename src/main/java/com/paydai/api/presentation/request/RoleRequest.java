package com.paydai.api.presentation.request;

import com.paydai.api.domain.model.WorkspaceModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleRequest {
  private String role;
  private UUID workspaceId;
}
