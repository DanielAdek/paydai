package com.paydai.api.presentation.controller.Impl;

import com.paydai.api.domain.service.AdminService;
import com.paydai.api.presentation.controller.AdminController;
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

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/admin/paydai")
@RequiredArgsConstructor
@Tag(name = "Paydai", description = "Stripe Account APIs tag")
public class AdminControllerImpl implements AdminController {
  private final AdminService service;

  @Operation(
    summary = "Stripe connected account retrieve API endpoint",
    description = "GET response to show stripe Ids"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = JapiResponse.class), mediaType = "application/json")}),
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
  })
  @Override
//  @SecurityRequirements({
//    @SecurityRequirement(name = "Authorization", scopes = {"read", "write"})
//  })
  @GetMapping(path = "/stripe/connected/accounts")
  public ResponseEntity<JapiResponse> getAllUsersStripeAccounts() throws StripeException {
    JapiResponse response = service.retrieveAllUsersStripeAccount();
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "Stripe connected account delete API endpoint",
    description = "DELETE response to confirm deletion"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = JapiResponse.class), mediaType = "application/json")}),
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
  })
  @Override
//  @SecurityRequirements({
//    @SecurityRequirement(name = "Authorization", scopes = {"read", "write"})
//  })
  @DeleteMapping(path = "/stripe/connected/remove")
  public ResponseEntity<JapiResponse> removeConnectedStripeAccount(List<String> connectedAccounts) throws StripeException {
    JapiResponse response = service.deleteConnectedAccount(connectedAccounts);
    return new ResponseEntity<>(response, response.getStatusCode());
  }
}
