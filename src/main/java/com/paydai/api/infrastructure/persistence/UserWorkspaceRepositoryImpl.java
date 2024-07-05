package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.UserWorkspaceModel;
import com.paydai.api.domain.repository.UserWorkspaceRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserWorkspaceRepositoryImpl extends UserWorkspaceRepository, JpaRepository<UserWorkspaceModel, UUID> {
  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM user_workspace_bridge_tbl WHERE user_id=?1")
  List<UserWorkspaceModel> findByUserId(UUID userId);
}
