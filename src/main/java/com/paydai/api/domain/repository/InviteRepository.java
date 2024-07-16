package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.InviteModel;

import java.util.UUID;

public interface InviteRepository {
  InviteModel save(InviteModel buildInvite);
  InviteModel findByInvite(String code);
  void removeInvite(String code);
  InviteModel findByInvited(UUID roleId, UUID workspaceId, String companyEmail);
}
