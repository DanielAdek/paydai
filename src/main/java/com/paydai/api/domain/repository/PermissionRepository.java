package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.PermissionModel;
import com.paydai.api.presentation.request.PermissionRequest;

import java.util.List;

public interface PermissionRepository {
  List<PermissionModel> findAllPermissions();
  PermissionModel save(PermissionModel buildPermission);
}
