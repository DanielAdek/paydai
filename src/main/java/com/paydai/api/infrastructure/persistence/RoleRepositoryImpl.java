package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.RoleModel;
import com.paydai.api.domain.repository.RoleRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface RoleRepositoryImpl extends RoleRepository, JpaRepository<RoleModel, UUID> {
  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM role_tbl WHERE workspace_id=?1")
  List<RoleModel> findRolesByWorkspace(UUID workspaceId);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM role_tbl WHERE role=?1 AND workspace_id=?2")
  RoleModel findRoleByWorkspace(String role, UUID workspaceId);
}
