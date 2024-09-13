package com.paydai.api.presentation.controller.Impl;

import com.paydai.api.domain.service.PayoutTxnService;
import com.paydai.api.presentation.controller.PayoutTxnController;
import com.paydai.api.presentation.request.PayoutRequest;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/transaction/")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Payout transactions APIs tag")
public class PayoutTxnControllerImpl implements PayoutTxnController {
  private final PayoutTxnService service;

  @Operation(
    summary = "Payout transactions retrieve API endpoint",
    description = "GET response to show auth DTO"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = JapiResponse.class), mediaType = "application/json")}),
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
  })
  @Override
  @SecurityRequirements({
    @SecurityRequirement(name = "Authorization", scopes = {"read", "write"})
  })
  @PostMapping("debit/initiate")
  public ResponseEntity<JapiResponse> createPayoutTransaction(@RequestBody PayoutRequest payload) throws StripeException {
    JapiResponse response = service.createPayout(payload);
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "Payout transactions if logged in as personal: retrieve API endpoint",
    description = "GET response to show auth DTO"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = JapiResponse.class), mediaType = "application/json")}),
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
  })
  @Override
  @SecurityRequirements({
    @SecurityRequirement(name = "Authorization", scopes = {"read", "write"})
  })
  @GetMapping("/user")
  public ResponseEntity<JapiResponse> getPayoutTransactions(@RequestParam UUID userId) {
    JapiResponse response = service.getUserPayoutTransactions(userId);
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "Payout transactions overview: retrieve API endpoint",
    description = "GET response to show transaction overview"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = JapiResponse.class), mediaType = "application/json")}),
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
  })
  @Override
  @SecurityRequirements({
    @SecurityRequirement(name = "Authorization", scopes = {"read", "write"})
  })
  @GetMapping("/user/workspace")
  public ResponseEntity<JapiResponse> getPayoutTransactions(UUID userId, UUID workspaceId) {
    JapiResponse response = service.getUserPayoutTransactions(userId, workspaceId);
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "Payout transactions overview: retrieve API endpoint",
    description = "GET response to show transaction overview"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = JapiResponse.class), mediaType = "application/json")}),
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
  })
  @Override
  @SecurityRequirements({
    @SecurityRequirement(name = "Authorization", scopes = {"read", "write"})
  })
  @GetMapping("/credit/top-up")
  public ResponseEntity<JapiResponse> topUpBalance(@RequestParam double amount, @RequestParam String currency) throws StripeException {
    JapiResponse response = service.topUpAccount(amount, currency);
    return new ResponseEntity<>(response, response.getStatusCode());
  }
}
