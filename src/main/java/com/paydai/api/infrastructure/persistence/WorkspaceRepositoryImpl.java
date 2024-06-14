package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.WorkspaceModel;
import com.paydai.api.domain.repository.WorkspaceRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface WorkspaceRepositoryImpl extends WorkspaceRepository, JpaRepository<WorkspaceModel, UUID> {
  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM workspace_tbl WHERE name =?1")
  WorkspaceModel findByName(String name);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM workspace_tbl WHERE workspaceId=?1")
  WorkspaceModel findByWorkspaceId(UUID workspaceId);
}
