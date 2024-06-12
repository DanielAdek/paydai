package com.paydai.api.presentation.dto.permission;

import com.paydai.api.domain.model.PermissionModel;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PermissionDtoMapper implements Function<PermissionModel, com.paydai.api.presentation.dto.permission.WorkspaceRecord> {
  @Override
  public com.paydai.api.presentation.dto.permission.WorkspaceRecord apply(PermissionModel permissionModel) {
    return new com.paydai.api.presentation.dto.permission.WorkspaceRecord(
      permissionModel.getPermissionId(),
      permissionModel.getPermission(),
      permissionModel.getCreatedAt(),
      permissionModel.getUpdatedAt()
    );
  }
}
