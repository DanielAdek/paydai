package com.paydai.api.presentation.dto.workspace;

import com.paydai.api.domain.model.WorkspaceModel;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class WorkspaceDtoMapper implements Function<WorkspaceModel, WorkspaceRecord> {
  @Override
  public WorkspaceRecord apply(WorkspaceModel workspaceModel) {
    return new WorkspaceRecord(
      workspaceModel.getWorkspaceId(),
      workspaceModel.getName(),
      workspaceModel.getCreatedAt(),
      workspaceModel.getUpdatedAt()
    );
  }
}
