package com.paydai.api.domain.repository;

import com.paydai.api.domain.model.EmailModel;

import java.util.Optional;

public interface EmailRepository {
  Optional<EmailModel> findByEmail(String email);
  EmailModel findEmailQuery(String email);

  EmailModel save(EmailModel buildEmail);
}
