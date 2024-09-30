package com.paydai.api.presentation.controller.Impl;

import com.paydai.api.domain.model.InvoiceStatus;
import com.paydai.api.domain.service.InvoiceService;
import com.paydai.api.infrastructure.config.AppConfig;
import com.paydai.api.presentation.controller.InvoiceController;
import com.paydai.api.presentation.request.InvoiceRequest;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/invoice")
@Tag(name = "Invoice", description = "APIs for invoices")
public class InvoiceControllerImpl implements InvoiceController {
  private final InvoiceService service;
  private final AppConfig config;

  @PostConstruct
  public void init() { Stripe.apiKey = config.getStripeKey(); }

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
  @GetMapping("customer")
  public ResponseEntity<JapiResponse> getInvoicesToCustomer(@RequestParam UUID customerId) {
    JapiResponse response = service.getInvoiceToCustomer(customerId);
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "Get one Invoice API endpoint",
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
  public ResponseEntity<JapiResponse> getInvoice(@RequestParam String invoiceCode) {
    JapiResponse response = service.getInvoice(invoiceCode);
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
  public ResponseEntity<JapiResponse> getWorkspaceInvoicesToCustomers(@RequestParam UUID workspaceId) {
    JapiResponse response = service.getWorkspaceInvoicesToCustomers(workspaceId);
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "Finalize invoice API endpoint",
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
  @GetMapping("finalize")
  public ResponseEntity<JapiResponse> finalizeInvoice(@RequestParam String invoiceCode) throws StripeException {
    JapiResponse response = service.finalizeInvoice(invoiceCode);
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "Send invoice API endpoint",
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
  @GetMapping("send")
  public ResponseEntity<JapiResponse> sendInvoice(@RequestParam String invoice) throws StripeException {
    JapiResponse response = service.sendInvoice(invoice);
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "filter invoice by status API endpoint",
    description = "GET response to show invoice DTO, the invoice data"
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
  @GetMapping("filter")
  public ResponseEntity<JapiResponse> filterInvoices(@RequestParam UUID workspaceId, @RequestParam List<InvoiceStatus> status) {
    JapiResponse response = service.filterInvoice(workspaceId, status);
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "Sales reps invoiced by status API endpoint",
    description = "GET response to show invoice DTO, the invoice data"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = JapiResponse.class), mediaType = "application/json")}),
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
  })
  @SecurityRequirements({
    @SecurityRequirement(name = "Authorization", scopes = {"read", "write"})
  })
  @Override
  @GetMapping("/sales-rep/involved")
  public ResponseEntity<JapiResponse> getSalesRepInvolvedInvoice(@RequestParam String invoiceCode) {
    JapiResponse response = service.getSalesRepInvoice(invoiceCode);
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "Cancel invoice API endpoint",
    description = "GET response to show invoice DTO, the invoice data"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = JapiResponse.class), mediaType = "application/json")}),
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
  })
  @SecurityRequirements({
    @SecurityRequirement(name = "Authorization", scopes = {"read", "write"})
  })
  @Override
  @DeleteMapping("cancel")
  public ResponseEntity<JapiResponse> cancelInvoice(@RequestParam String invoiceCode) {
    JapiResponse response = service.cancelInvoice(invoiceCode);
    return new ResponseEntity<>(response, response.getStatusCode());
  }
}
