package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.PermissionModel;
import com.paydai.api.domain.repository.PermissionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PermissionRepositoryImpl extends PermissionRepository, JpaRepository<PermissionModel, UUID> {
  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM permission_tbl")
  List<PermissionModel> findAllPermissions();
}
