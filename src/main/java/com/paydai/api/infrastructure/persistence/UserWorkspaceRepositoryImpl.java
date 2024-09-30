package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.UserWorkspaceModel;
import com.paydai.api.domain.repository.UserWorkspaceRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserWorkspaceRepositoryImpl extends UserWorkspaceRepository, JpaRepository<UserWorkspaceModel, UUID> {
  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM user_workspace_bridge_tbl WHERE user_id=?1 AND removed=false")
  List<UserWorkspaceModel> findByUserId(UUID userId);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM user_workspace_bridge_tbl WHERE workspace_id=?1 and role_id=?2 AND removed=false")
  List<UserWorkspaceModel> findUsersByWorkspaceId(UUID workspaceId, Optional<UUID> roleId);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM user_workspace_bridge_tbl WHERE user_id=?1 AND removed=false")
  UserWorkspaceModel findUserWorkspaceRole(UUID userId);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM user_workspace_bridge_tbl WHERE user_id=?1 AND workspace_id=?2 AND removed=false")
  UserWorkspaceModel findOneByUserId(UUID userId, UUID workspaceId);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM user_workspace_bridge_tbl WHERE workspace_id=?1 AND removed=false")
  List<UserWorkspaceModel> findUsersByWorkspaceId(UUID workspaceId);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM user_workspace_bridge_tbl WHERE email_id=?1 AND removed=false")
  UserWorkspaceModel findUserByEmail(UUID emailId);

  @Override
  @Query(nativeQuery = true, value = "SELECT  * FROM user_workspace_bridge_tbl WHERE role_id=?1 AND workspace_id=?2 AND removed=false")
  List<UserWorkspaceModel> findUserByRoleWorkspaces(UUID roleId, UUID workspaceId);

  @Modifying
  @Transactional
  @Override
  @Query(nativeQuery = true, value = "UPDATE user_workspace_bridge_tbl SET removed=true WHERE user_id=?1 AND workspace_id=?2")
  void removeSalesRep(UUID userId, UUID workspaceId);
}
