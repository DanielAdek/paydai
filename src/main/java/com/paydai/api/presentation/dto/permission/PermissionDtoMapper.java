package com.paydai.api.presentation.dto.permission;

import com.paydai.api.domain.model.PermissionModel;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PermissionDtoMapper implements Function<PermissionModel, PermissionRecord> {
  @Override
  public PermissionRecord apply(PermissionModel permissionModel) {
    return new PermissionRecord(
      permissionModel.getPermissionId(),
      permissionModel.getPermission(),
      permissionModel.getCreatedAt(),
      permissionModel.getUpdatedAt()
    );
  }
}
