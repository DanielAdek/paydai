package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.RoleModel;

import java.util.List;
import java.util.UUID;

public interface RoleRepository {
  RoleModel save(RoleModel buildRole);

  RoleModel findRole(String role);

  List<RoleModel> findRoles();
}
