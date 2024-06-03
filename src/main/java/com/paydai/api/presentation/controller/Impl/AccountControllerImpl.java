package com.paydai.api.presentation.controller.Impl;

import com.paydai.api.domain.service.AccountService;
import com.paydai.api.infrastructure.config.AppConfig;
import com.paydai.api.presentation.controller.AccountController;
import com.paydai.api.presentation.request.AccountLinkRequest;
import com.paydai.api.presentation.request.AccountRequest;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.Stripe;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/")
@RequiredArgsConstructor
@Tag(name = "Account", description = "Stripe Account APIs tag")
public class AccountControllerImpl implements AccountController {
  private final AccountService service;
  private final AppConfig config;

  @PostConstruct
  public void init() { Stripe.apiKey = config.getStripeKey(); }

  @Operation(
    summary = "Stripe account create API endpoint",
    description = "POST response to show auth DTO, the auth-data and token"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = JapiResponse.class), mediaType = "application/json")}),
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
  })
  @Override
  @PostMapping(path = "/account")
  public ResponseEntity<JapiResponse> createAccount(@RequestBody() AccountRequest payload) {
    JapiResponse response = service.createAccount(payload);
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Override
  @PostMapping(path = "/account_link")
  public ResponseEntity<JapiResponse> createAccountLink(@RequestBody AccountLinkRequest payload) {
    JapiResponse response = service.createAccountLink(payload);
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Override
  @GetMapping(path = "/")
  public String serveIndex() {
    return service.serveIndex();
  }
}