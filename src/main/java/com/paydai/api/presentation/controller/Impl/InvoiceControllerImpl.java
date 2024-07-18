package com.paydai.api.presentation.controller.Impl;

import com.paydai.api.domain.service.InvoiceService;
import com.paydai.api.presentation.controller.InvoiceController;
import com.paydai.api.presentation.request.InvoiceRequest;
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
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/invoice")
@Tag(name = "Invoice", description = "APIs for invoices")
public class InvoiceControllerImpl implements InvoiceController {
  private final InvoiceService service;
  @Operation(
    summary = "Invoice create API endpoint",
    description = "POST response to show auth DTO, the invoice data"
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
  @PostMapping(path = "/create")
  public ResponseEntity<JapiResponse> create(@RequestBody() InvoiceRequest payload) throws StripeException {
    JapiResponse response = service.create(payload);
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "Invoice list for customer API endpoint",
    description = "POST response to show auth DTO, the invoice data"
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
  @GetMapping
  public ResponseEntity<JapiResponse> getInvoicesToCustomer(UUID customerId) {
    JapiResponse response = service.getInvoiceToCustomer(customerId);
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "Invoice list for workspace API endpoint",
    description = "POST response to show auth DTO, the invoice data"
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
  @GetMapping("workspace")
  public ResponseEntity<JapiResponse> getWorkspaceInvoicesToCustomers(UUID workspaceId) {
    JapiResponse response = service.getWorkspaceInvoicesToCustomers(workspaceId);
    return new ResponseEntity<>(response, response.getStatusCode());
  }
}
