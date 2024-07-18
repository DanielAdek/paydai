package com.paydai.api.presentation.dto.userWorkspace;

import com.paydai.api.domain.model.UserWorkspaceModel;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class TeamsDtoMapper implements Function<UserWorkspaceModel, TeamsRecord> {

  @Override
  public TeamsRecord apply(UserWorkspaceModel userWorkspaceModel) {
    return new TeamsRecord(
      userWorkspaceModel.getUser().getId(),
      userWorkspaceModel.getUser().getFirstName(),
      userWorkspaceModel.getUser().getLastName(),
      userWorkspaceModel.getEmail().getEmail(),
      userWorkspaceModel.getRole().getId(),
      userWorkspaceModel.getRole().getRole(),
      userWorkspaceModel.getWorkspace().getId(),
      userWorkspaceModel.getWorkspace().getName(),
      userWorkspaceModel.getCommission().getId(),
      userWorkspaceModel.getCommission().getInterval() + " " + userWorkspaceModel.getCommission().getIntervalUnit(),
      userWorkspaceModel.getCommission().getCommission(),
      userWorkspaceModel.getId()
    );
  }
}
