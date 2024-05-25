package com.paydai.api.infrastructure.config;

import com.paydai.api.domain.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityBeanConfig {
  private final AccountRepository repository;

  @Autowired
  public SecurityBeanConfig(AccountRepository repository) {
    this.repository = repository;
  }

}
