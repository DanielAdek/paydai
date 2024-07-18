package com.paydai.api.presentation.dto.userWorkspace;

import com.paydai.api.domain.model.UserWorkspaceModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamsDto {
  private UUID userId;
  private String firstName;
  private String lastName;
  private String email;
  private UUID roleId;
  private String Role;
  private UUID workspaceId;
  private String workspace;
  private UUID commSettingId;
  private int interval;
  private String intervalUnit;
  private UUID userWorkspaceId;

  public TeamsDto getTeamsDtoDetails(UserWorkspaceModel userWorkspaceModel) {
    return new TeamsDto(userWorkspaceModel.getUser().getId(),
      userWorkspaceModel.getUser().getFirstName(),
      userWorkspaceModel.getUser().getLastName(),
      userWorkspaceModel.getEmail().getEmail(),
      userWorkspaceModel.getRole().getId(),
      userWorkspaceModel.getRole().getRole(),
      userWorkspaceModel.getWorkspace().getId(),
      userWorkspaceModel.getWorkspace().getName(),
      userWorkspaceModel.getCommission().getId(),
      userWorkspaceModel.getCommission().getInterval(),
      userWorkspaceModel.getCommission().getIntervalUnit(),
      userWorkspaceModel.getId()
    );
  }
}
