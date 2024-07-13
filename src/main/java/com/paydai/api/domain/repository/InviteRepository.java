package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.InviteModel;

public interface InviteRepository {
  InviteModel save(InviteModel buildInvite);
  InviteModel findByInvite(String code);
  void removeInvite(String code);
}
