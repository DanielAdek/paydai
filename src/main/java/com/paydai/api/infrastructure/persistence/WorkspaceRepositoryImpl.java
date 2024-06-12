package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.WorkspaceModel;
import com.paydai.api.domain.repository.WorkspaceRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkspaceRepositoryImpl extends WorkspaceRepository, JpaRepository<WorkspaceModel, UUID> {
}
