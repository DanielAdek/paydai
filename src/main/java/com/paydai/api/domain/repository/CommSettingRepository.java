package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.CommissionSettingModel;

import java.util.UUID;

public interface CommSettingRepository {
  CommissionSettingModel findCommissionByWorkSpace(UUID workspaceId);

  CommissionSettingModel save(CommissionSettingModel buildCommSetting);
}
