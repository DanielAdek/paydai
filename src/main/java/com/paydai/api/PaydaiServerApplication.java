package com.paydai.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.paydai.api.infrastructure.config.DotEnvConfig;

@SpringBootApplication
public class PaydaiServerApplication {

  public static void main(String[] args) {
    SpringApplication application = new SpringApplication(PaydaiServerApplication.class);
    application.addInitializers(new DotEnvConfig.DotenvInitializer());
    application.run(args);
  }
}
