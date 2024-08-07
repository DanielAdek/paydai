package com.paydai.api.infrastructure.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class AppConfig {
  // SECTION FOR URLs
  @Value("${application.server.url}")
  private String apiBaseUrl;

  @Value("${application.paydai.client_callback}")
  private String paydaiClientBaseUrl;


  // SECTION FOR STRIPE
  @Value("${application.stripe.secret_key}")
  private String stripeKey;

  @Value("${application.stripe.base_url}")
  private String stripeBaseApi;

  @Value("${application.stripe.webhook.secret.balance}")
  private String stripeWebhookSecretBal;

  @Value("${application.stripe.webhook.secret.transfer}")
  private String stripeWebhookSecretTransfer;

  @Value("${application.stripe.webhook.secret.invoice_connect}")
  private String stripeWebhookSecretInvoiceConnect;


  // OTHERS
  @Value("${application.security.jwt_secret.key}")
  private String secretKey;

  @Value("${spring.mail.username}")
  private String from;
}
