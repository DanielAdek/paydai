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
}
