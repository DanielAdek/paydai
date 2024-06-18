package com.paydai.api.presentation.controller.Impl;

import com.paydai.api.domain.service.InviteService;
import com.paydai.api.presentation.controller.InviteController;
import com.paydai.api.presentation.request.AccountRequest;
import com.paydai.api.presentation.request.InviteRequest;
import com.paydai.api.presentation.response.JapiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/invite")
@Tag(name = "Invite", description = "APIs for invitation")
public class InviteControllerImpl implements InviteController {
  private final InviteService service;

  @Operation(
    summary = "Stripe account create API endpoint",
    description = "POST response to show auth DTO, the auth-data and token"
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
  @PostMapping(path = "/send")
  public ResponseEntity<JapiResponse> sendInvite(@RequestBody InviteRequest request) throws MessagingException {
    JapiResponse response = service.createInvite(request);
    return new ResponseEntity<>(response, response.getStatusCode());
  }
}
