package com.paydai.api.presentation.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {
  private String name;
  private String phone;
  private String email;
  private UUID setterId;
  private UUID closerId;
  private String description;
  private UUID workspaceId;
  private String creatorRole;
}
