package com.paydai.api.presentation.controller.Impl;

import com.paydai.api.domain.service.PayoutLedgerService;
import com.paydai.api.presentation.controller.PayoutLedgerController;
import com.paydai.api.presentation.response.JapiResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/transactions/")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Payout transactions APIs tag")
public class PayoutLedgerControllerImpl implements PayoutLedgerController {
  private final PayoutLedgerService service;

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
  @GetMapping("payout")
  public ResponseEntity<JapiResponse> getPayoutLedgerTransactions(@RequestParam UUID userId, @RequestParam UUID workspaceId) {
    JapiResponse response = service.getPayoutLedgerTransactions(userId, workspaceId);
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
  @GetMapping("payout/user")
  public ResponseEntity<JapiResponse> getPayoutLedgerTransactions(@RequestParam UUID userId) {
    JapiResponse response = service.getPayoutLedgerTransactions(userId);
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
  @GetMapping("payout/sales-rep/overview")
  public ResponseEntity<JapiResponse> getPayoutTransactionOverview(UUID userId, UUID workspaceId) {
    JapiResponse response = service.getTransactionOverview(userId, workspaceId);
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
  @GetMapping("payout/user/overview")
  public ResponseEntity<JapiResponse> getPayoutTransactionOverview(UUID userId) {
    JapiResponse response = service.getTransactionOverview(userId);
    return new ResponseEntity<>(response, response.getStatusCode());
  }
}
