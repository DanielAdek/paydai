package com.paydai.api.presentation.dto.email;

import com.paydai.api.domain.model.EmailModel;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class EmailDtoMapper implements Function<EmailModel, EmailRecord> {
  @Override
  public EmailRecord apply(EmailModel emailModel) {
    return new EmailRecord(
      emailModel.getId(),
      emailModel.getEmail(),
      emailModel.getEmailType(),
      emailModel.getCreatedAt()
    );
  }
}
