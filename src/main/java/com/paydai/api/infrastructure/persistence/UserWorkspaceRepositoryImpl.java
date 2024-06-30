package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.UserWorkspaceModel;
import com.paydai.api.domain.repository.UserWorkspaceRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserWorkspaceRepositoryImpl extends UserWorkspaceRepository, JpaRepository<UserWorkspaceModel, UUID> {
}
