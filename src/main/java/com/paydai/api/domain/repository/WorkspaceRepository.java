package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.WorkspaceModel;

public interface WorkspaceRepository {
  WorkspaceModel save(WorkspaceModel workspace);
}
