package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.UserWorkspaceModel;

public interface UserWorkspaceRepository {
  UserWorkspaceModel save(UserWorkspaceModel buildUserWorkspace);
}
