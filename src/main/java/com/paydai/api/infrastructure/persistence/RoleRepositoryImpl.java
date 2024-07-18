package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.RoleModel;
import com.paydai.api.domain.repository.RoleRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface RoleRepositoryImpl extends RoleRepository, JpaRepository<RoleModel, UUID> {
  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM role_tbl")
  List<RoleModel> findRoles();

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM role_tbl WHERE role=?1")
  RoleModel findRole(String role);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM role_tbl WHERE role_id=?1")
  RoleModel findByRoleId(UUID roleId);
}
