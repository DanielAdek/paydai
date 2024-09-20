package com.paydai.api.presentation.controller.Impl;

import com.paydai.api.domain.service.AuthService;
import com.paydai.api.presentation.controller.AuthController;
import com.paydai.api.presentation.request.AuthRequest;
import com.paydai.api.presentation.request.RegisterRequest;
import com.paydai.api.presentation.response.JapiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "APIs for onboarding and authentication")
public class AuthControllerImpl implements AuthController {
  private final AuthService service;

  @Operation(
    summary = "Paydai account create API endpoint",
    description = "POST response to show auth DTO, the auth-data and token"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = JapiResponse.class), mediaType = "application/json")}),
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
  })
  @Override
  @PostMapping(path = "/register")
  public ResponseEntity<JapiResponse> create(@RequestBody() RegisterRequest payload) {
    JapiResponse response = service.create(payload);
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "Paydai account login API endpoint",
    description = "POST response to show auth DTO, the auth-data and token"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = JapiResponse.class), mediaType = "application/json")}),
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
  })
  @Override
  @PostMapping(path = "/login")
  public ResponseEntity<JapiResponse> authenticate(@RequestBody AuthRequest payload) {
    JapiResponse response = service.authenticate(payload);
    return new ResponseEntity<>(response, response.getStatusCode());
  }
}