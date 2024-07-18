package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.CommissionSettingModel;
import com.paydai.api.domain.repository.CommSettingRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommSettingRepositoryImpl extends CommSettingRepository, JpaRepository<CommissionSettingModel, UUID> {
  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM commission_setting_tbl WHERE workspace_id=?1")
  CommissionSettingModel findCommissionByWorkSpace(UUID workspaceId);
}
