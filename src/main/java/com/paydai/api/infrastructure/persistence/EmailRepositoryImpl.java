package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.EmailModel;
import com.paydai.api.domain.model.EmailType;
import com.paydai.api.domain.repository.EmailRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailRepositoryImpl extends EmailRepository, JpaRepository<EmailModel, UUID> {
  @Override
  Optional<EmailModel> findByEmail(String email);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM email_tbl WHERE email = ?1 AND email_type='PERSONAL' AND access=true")
  EmailModel findEmailQuery(String email);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM email_tbl WHERE user_id=?1 AND email_type='PERSONAL' AND access=true")
  EmailModel findPersonalEmailByUser(UUID userId);

  @Override
  @Modifying
  @Transactional
  @Query(nativeQuery = true, value = "UPDATE email_tbl SET password_hash=?2 WHERE id=?1 AND email_type='PERSONAL'")
  void updateAuthPassword(UUID emailId, String password);
}
