package com.paydai.api.infrastructure.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DotEnvConfig {

  @Bean
  public static Dotenv dotenv() {
    return Dotenv.configure().ignoreIfMissing().load();
  }

  public static class DotenvInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
      Dotenv dotenv = dotenv();
      Map<String, String> envMap = new HashMap<>();
      for (DotenvEntry entry : dotenv.entries()) {
        envMap.put(entry.getKey(), entry.getValue());
      }
      envMap.forEach(System::setProperty);
    }
  }
}
