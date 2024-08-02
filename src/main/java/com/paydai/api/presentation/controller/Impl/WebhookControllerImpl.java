package com.paydai.api.presentation.controller.Impl;

import com.paydai.api.domain.exception.ApiRequestException;
import com.paydai.api.domain.service.WebhookService;
import com.paydai.api.infrastructure.config.AppConfig;
import com.paydai.api.presentation.controller.WebhookController;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/webhook")
@RequiredArgsConstructor
@Tag(name = "Webhook", description = "APIs for webhooks")
public class WebhookControllerImpl implements WebhookController {
  private final WebhookService service;
  private final AppConfig config;

  @PostConstruct
  public void init() { Stripe.apiKey = config.getStripeKey(); }

  @Operation(
    summary = "Connect webhook API endpoint",
    description = "Get response to show webhook DTO, the invoice data"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = JapiResponse.class), mediaType = "application/json")}),
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
  })
  @Override
  @PostMapping("invoice/connect")
  public ResponseEntity handleInvoiceEventConnectAccount(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signature) {
    try {
      Event event = Webhook.constructEvent(payload, signature, "whsec_e1D3moeHn2cSDpirELRrPI1rhOaLMLgw");
      JapiResponse response = service.handleInvoiceEventConnectAccount(payload, event);
      return ResponseEntity.status(response.getStatusCode()).body(response.getMessage());
    } catch (SignatureVerificationException e) {
//      log.error("⚠️  Webhook error while validating signature.", e);
      return ResponseEntity.status(400).body("Invalid signature");
    } catch (Exception e) {
      return ResponseEntity.status(500).body("Internal Server Exception");
    }
  }

  @Operation(
    summary = "Account webhook API endpoint",
    description = "Get response to show webhook DTO, the invoice data"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = JapiResponse.class), mediaType = "application/json")}),
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
  })
  @Override
  @PostMapping("transfer/account")
  public ResponseEntity handleTransferEventAccount(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signature){
    try {
      Event event = Webhook.constructEvent(payload, signature, "whsec_e1D3moeHn2cSDpirELRrPI1rhOaLMLgw");
      JapiResponse response = service.handleTransferEventAccount(payload, event);
      return ResponseEntity.ok(response.getMessage());
    } catch (SignatureVerificationException e) {
      log.error("⚠️  Webhook error while validating signature.", e);
      return ResponseEntity.status(400).body("Invalid signature");
    } catch (ApiRequestException e) {
      log.error("⚠️  Webhook error while validating signature.", e);
      return ResponseEntity.status(400).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(500).body("Internal Server Exception");
    }
  }
}
