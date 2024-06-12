package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.PasswordModel;

public interface PasswordRepository {
  PasswordModel save(PasswordModel buildPass);
}
