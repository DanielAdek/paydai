package com.paydai.api.presentation.controller.Impl;

import com.paydai.api.domain.service.TransactionService;
import com.paydai.api.presentation.controller.TransactionController;
import com.paydai.api.presentation.request.TransferRequest;
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
@RequestMapping(path = "/api/v1/transactions/")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Payout transactions APIs tag")
public class TransactionControllerImpl implements TransactionController {
  private final TransactionService service;

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
    JapiResponse response = service.getTransactions(userId, workspaceId);
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
    JapiResponse response = service.getTransactions(userId);
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
  public ResponseEntity<JapiResponse> getPayoutTransactionOverview(@RequestParam UUID userId, @RequestParam UUID workspaceId) {
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
  public ResponseEntity<JapiResponse> getPayoutTransactionOverview(@RequestParam UUID userId) {
    JapiResponse response = service.getTransactionOverview(userId);
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "Direct transfer transactions  API endpoint",
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
  @PostMapping("direct/merchant/sales-rep")
  public ResponseEntity<JapiResponse> directTransferToSalesRep(@RequestBody TransferRequest payload) throws StripeException {
    JapiResponse response = service.directTransferToSalesRep(payload);
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
  @GetMapping("merchant")
  public ResponseEntity<JapiResponse> getTransactionsMerchant(@RequestParam UUID workspaceId) {
    JapiResponse response = service.getTransactionsMerchantUse(workspaceId);
    return new ResponseEntity<>(response, response.getStatusCode());
  }
}
