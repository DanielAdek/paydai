package com.paydai.api.infrastructure.external;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestApiConfigBean {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
