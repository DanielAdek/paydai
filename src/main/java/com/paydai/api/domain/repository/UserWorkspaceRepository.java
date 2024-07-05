package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.UserWorkspaceModel;

import java.util.List;
import java.util.UUID;

public interface UserWorkspaceRepository {
  UserWorkspaceModel save(UserWorkspaceModel buildUserWorkspace);
  List<UserWorkspaceModel> findByUserId(UUID userId);
}
