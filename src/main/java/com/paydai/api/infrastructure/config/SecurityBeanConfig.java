package com.paydai.api.infrastructure.config;

import com.paydai.api.domain.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityBeanConfig {
  private final AuthRepository repository;

  @Autowired
  public SecurityBeanConfig(AuthRepository repository) {
    this.repository = repository;
  }

}
