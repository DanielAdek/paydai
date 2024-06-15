package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.InviteModel;
import com.paydai.api.domain.repository.InviteRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface InviteRepositoryImpl extends InviteRepository, JpaRepository<InviteModel, UUID> {
  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM invite_tbl WHERE invite_code=?1")
  InviteModel findByInvite(String code);
}
