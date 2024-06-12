package com.paydai.api.infrastructure.persistence;

import com.paydai.api.domain.model.EmailModel;
import com.paydai.api.domain.repository.EmailRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailRepositoryImpl extends EmailRepository, JpaRepository<EmailModel, UUID> {
  @Override
  Optional<EmailModel> findByEmail(String email);

  @Override
  @Query(nativeQuery = true, value = "SELECT * FROM email_tbl WHERE email = ?1")
  EmailModel findEmailQuery(String email);

  @Override
  @Query(nativeQuery = true, value = "SELECT e.email, p.password_hash FROM email_tbl e inner join passwords_tbl p on e.email_id=p.email_id WHERE email = ?1")
  EmailModel findEmailAndPassword(String email);
}