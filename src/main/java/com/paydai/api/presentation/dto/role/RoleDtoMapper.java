package com.paydai.api.presentation.dto.role;

import com.paydai.api.domain.model.RoleModel;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class RoleDtoMapper implements Function<RoleModel, RoleRecord> {
  @Override
  public RoleRecord apply(RoleModel roleModel) {
    return new RoleRecord(
      roleModel.getRoleId(),
      roleModel.getRole().toUpperCase(),
      roleModel.getCreatedAt(),
      roleModel.getUpdatedAt()
    );
  }
}
