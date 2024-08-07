package com.paydai.api.presentation.controller.Impl;

import com.paydai.api.domain.service.AccountLedgerService;
import com.paydai.api.presentation.controller.AccountLedgerController;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/account/ledger")
@RequiredArgsConstructor
@Tag(name = "Ledger", description = "Ledger Account APIs tag")
public class AccountLedgerControllerImpl implements AccountLedgerController {
  private final AccountLedgerService service;

  @Operation(
    summary = "Ledger account retrieve API endpoint",
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
  @GetMapping("/user/workspace")
  public ResponseEntity<JapiResponse> getUserAccountLedger(UUID userId, UUID workspaceId) {
    JapiResponse response = service.getUserAccountLedger(userId, workspaceId);
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "Ledger account retrieve all API endpoint",
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
  public ResponseEntity<JapiResponse> getUserAccountsLedger(UUID userId) {
    JapiResponse response = service.getUserAccountsLedger(userId);
    return new ResponseEntity<>(response, response.getStatusCode());
  }
}
