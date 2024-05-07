package com.paydai.api.infrastructure.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.security.SecurityScheme(
  name = "Authorization",
  description = "JWT auth description",
  scheme = "bearer",
  type = SecuritySchemeType.HTTP,
  bearerFormat = "JWT",
  in = SecuritySchemeIn.HEADER
)
public class SwaggerDocConfig {
  private final AppConfig config;

  @Bean
  public OpenAPI SwaggerServerConfiguration() {
    return new OpenAPI().info(getDocInfoConfig()).servers(List.of(getServerBaseEndpointConfig())).security(List.of(getSecurityRequirement()));
  }

  private Server getServerBaseEndpointConfig() {
    Server server = new Server();
    server.setUrl(config.getApiBaseUrl());
    return server;
  }

  private Contact getDeveloperContactConfig() {
    Contact contact = new Contact();
    contact.setEmail("maildaniel.me1@gmail.com");
    contact.setName("DanielAdek");
    contact.setUrl("https://www.linkedin.com/in/daniel-adek");
    return contact;
  }

  private License getLicenceConfig() {
    return new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");
  }

  private Info getDocInfoConfig() {
    return new Info().title("PAYDAI SERVER API").version("1.0").contact(getDeveloperContactConfig())
      .description("This API exposes endpoints to manage the emporium application APIs.")
      .license(getLicenceConfig());
  }

  private SecurityRequirement getSecurityRequirement() {
    SecurityRequirement securityRequirement = new SecurityRequirement();
    securityRequirement.addList("name", "token");
    return securityRequirement;
  }
}