package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.UserWorkspaceModel;
import com.paydai.api.presentation.dto.userWorkspace.TeamsRecord;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserWorkspaceRepository {
  UserWorkspaceModel save(UserWorkspaceModel buildUserWorkspace);
  List<UserWorkspaceModel> findByUserId(UUID userId);
  UserWorkspaceModel findOneByUserId(UUID userId, UUID workspaceId);
  List<UserWorkspaceModel> findUsersByWorkspaceId(UUID workspaceId, Optional<UUID> roleId);
  UserWorkspaceModel findUserWorkspaceRole(UUID userId);
  List<UserWorkspaceModel> findUsersByWorkspaceId(UUID workspaceId);
  UserWorkspaceModel findUserByEmail(UUID emailId);
  List<UserWorkspaceModel> findUserByRoleWorkspaces(UUID roleId, UUID workspaceId);
}
