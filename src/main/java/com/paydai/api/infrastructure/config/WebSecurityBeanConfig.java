package com.paydai.api.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityBeanConfig {
//  private final JwtAuthFilter jwtAuthFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
      .csrf(AbstractHttpConfigurer::disable)
      .authorizeHttpRequests(
        req -> req.requestMatchers(
          "api/v1/**",
            "/swagger-ui/**",
            "/v3/api-docs/**"
          )
          .permitAll()
          .anyRequest()
          .authenticated())
      .sessionManagement(ses -> ses.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//      .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
      .build();
  }
}
