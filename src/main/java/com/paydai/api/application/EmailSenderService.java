package com.paydai.api.application;

import com.paydai.api.infrastructure.config.AppConfig;
import com.paydai.api.presentation.request.EmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailSenderService {
  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;
  private final AppConfig appConfig;

  @Async
  public void sendMail(EmailRequest request) throws MessagingException {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

    mimeMessageHelper.setFrom(appConfig.getFrom());
    mimeMessageHelper.setTo(request.getToEmail());
    mimeMessageHelper.setSubject(request.getSubject());

    if(request.isHTML()) {
      Context context = new Context();
      /*
        content is the variable defined in our HTML template within the div tag
      */
      context.setVariable("link", request.getMessage());
      String processedString = templateEngine.process("invite.template", context);

      mimeMessageHelper.setText(processedString, true);
    } else {
      mimeMessageHelper.setText(request.getMessage(), false);
    }

    mailSender.send(mimeMessage);
  }
}