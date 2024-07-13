package com.paydai.api.presentation.controller.Impl;

import com.paydai.api.domain.model.CustomerModel;
import com.paydai.api.domain.service.CustomerService;
import com.paydai.api.presentation.controller.CustomerController;
import com.paydai.api.presentation.request.CustomerRequest;
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

@RestController
@RequestMapping(path = "/api/v1/customer")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "APIs for Leads")
public class CustomerControllerImpl implements CustomerController {
  private final CustomerService service;

  @Operation(
    summary = "Customer create API endpoint",
    description = "POST response to show auth DTO, the customer data"
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
  public ResponseEntity<JapiResponse> create(@RequestBody CustomerRequest payload) {
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
  public ResponseEntity<JapiResponse> getCustomers() {
    JapiResponse response = service.getCustomers();
    return new ResponseEntity<>(response, response.getStatusCode());
  }
}
