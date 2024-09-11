package com.paydai.api.presentation.controller.Impl;

import com.paydai.api.domain.service.RefundService;
import com.paydai.api.presentation.controller.RefundController;
import com.paydai.api.presentation.request.RefundRequest;
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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/refund")
@RequiredArgsConstructor
@Tag(name = "Refund", description = "Refund APIs tag")
public class RefundControllerImpl implements RefundController {
  private final RefundService service;

  @Operation(
    summary = "Request Refund API endpoint",
    description = "POST response to show request DTO"
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
  @PostMapping
  public ResponseEntity<JapiResponse> create(@RequestBody RefundRequest payload) {
    JapiResponse response = service.create(payload);
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "Request Refund API endpoint",
    description = "POST response to show request DTO"
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
  @GetMapping("/requests")
  public ResponseEntity<JapiResponse> getRefundRequests(@RequestParam UUID workspaceId) {
    JapiResponse response = service.getRefundRequests(workspaceId);
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "Request Refunds Merchant API endpoint",
    description = "POST response to show request DTO"
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
  @GetMapping("/merchant/requests")
  public ResponseEntity<JapiResponse> getRefundsRequests(@RequestParam UUID workspaceId) {
    JapiResponse response = service.getRefundsRequests(workspaceId);
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "Request Refund API endpoint",
    description = "POST response to show request DTO"
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
  @GetMapping("/liability")
  public ResponseEntity<JapiResponse> getLiabilityBalance(@RequestParam UUID workspaceId) {
    JapiResponse response = service.getLiabilityBalance(workspaceId);
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "Request Refund API endpoint",
    description = "POST response to show request DTO"
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
  @GetMapping("/liability/user")
  public ResponseEntity<JapiResponse> getTotalLiability() {
    JapiResponse response = service.getLiabilityBalance();
    return new ResponseEntity<>(response, response.getStatusCode());
  }
}
