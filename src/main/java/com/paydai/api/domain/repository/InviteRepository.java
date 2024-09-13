package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.InviteModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InviteRepository {
  InviteModel save(InviteModel buildInvite);
  Optional<InviteModel> findByInvite(String code);
  void removeInvite(String code);
  void removeAssignedTeamMembers(String inviteCode);
  InviteModel findByInvited(UUID roleId, UUID workspaceId, String companyEmail);
  List<InviteModel> findWorkspaceInvites(UUID workspaceId);
}
