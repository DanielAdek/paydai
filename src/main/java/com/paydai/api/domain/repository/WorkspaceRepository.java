package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.WorkspaceModel;

import java.util.UUID;

public interface WorkspaceRepository {
  WorkspaceModel save(WorkspaceModel workspace);
  WorkspaceModel findByName(String name);
  WorkspaceModel findByWorkspaceId(UUID workspaceId);
  WorkspaceModel findByUserId(UUID userId);
}
