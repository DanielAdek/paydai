package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.EmailModel;

import java.util.Optional;
import java.util.UUID;

public interface EmailRepository {
  Optional<EmailModel> findByEmail(String email);
  EmailModel findEmailQuery(String email);
  EmailModel findEmailAndPassword(String email);
  EmailModel save(EmailModel buildEmail);
  EmailModel findPersonalEmailByUser(UUID userId);
}
