package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.InviteModel;
import com.paydai.api.domain.repository.InviteRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface InviteRepositoryImpl extends InviteRepository, JpaRepository<InviteModel, UUID> {
  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM invite_tbl WHERE invite_code=?1")
  InviteModel findByInvite(String code);

  @Override
  @Modifying
  @Transactional
  @Query(nativeQuery = true, value = "DELETE FROM invite_tbl WHERE invite_code=?1")
  void removeInvite(String code);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM invite_tbl WHERE id=?1 AND workspace_id=?2 And company_email=?3")
  InviteModel findByInvited(UUID roleId, UUID workspaceId, String companyEmail);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM invite_tbl WHERE workspace_id=?1")
  List<InviteModel> findWorkspaceInvites(UUID workspaceId);
}
