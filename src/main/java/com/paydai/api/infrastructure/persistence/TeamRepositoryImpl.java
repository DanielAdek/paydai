package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.TeamModel;
import com.paydai.api.domain.repository.TeamRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TeamRepositoryImpl extends TeamRepository, JpaRepository<TeamModel, UUID> {
  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM team_tbl WHERE manager_id=?1 AND (workspace_id=?2 OR user_workspace_id = ?2)")
  List<TeamModel> findByTeamManager(UUID managerId, UUID workspaceId);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM team_tbl WHERE member_id=?1 AND workspace_id=?2")
  TeamModel findByTeamMember(UUID memberId, UUID workspaceId);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM team_tbl WHERE member_id=?1 AND workspace_id=?2")
  List<TeamModel> findManyTeamMembers(UUID memberId, UUID workspaceId);
}
