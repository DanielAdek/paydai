package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.TeamModel;

import java.util.List;
import java.util.UUID;

public interface TeamRepository {
  TeamModel save(TeamModel buildTeam);
  List<TeamModel> findByTeamManager(UUID managerId, UUID workspaceId);
  TeamModel findByTeamMember(UUID memberId, UUID workspaceId);
  List<TeamModel> findManyTeamMembers(UUID memberId, UUID workspaceId);
}
