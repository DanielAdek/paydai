package com.paydai.api.infrastructure.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class AppConfig {
  // SECTION FOR OPEN API DOC
  @Value("${application.server.url}")
  private String apiBaseUrl;

  @Value("${application.stripe.secret_key}")
  private String stripeKey;

  @Value("${application.stripe.base_url}")
  private String stripeBaseApi;

  @Value("${application.paydai.client_callback}")
  private String paydaiClientBaseUrl;

  @Value("${application.security.jwt_secret.key}")
  private String secretKey;

  @Value("${spring.mail.username}")
  private String from;
}
