package com.paydai.api.presentation.controller.Impl;

import com.paydai.api.domain.service.ProfileService;
import com.paydai.api.presentation.controller.ProfileController;
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
@RequestMapping(path = "/api/v1/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "Profile Account APIs tag")
public class ProfileControllerImpl implements ProfileController {
  private final ProfileService service;

  @Operation(
    summary = "Switch between workspace API endpoint",
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
  @GetMapping("/switch/workspace")
  public ResponseEntity<JapiResponse> switchWorkspaceProfile(@RequestParam UUID workspaceId) {
    JapiResponse response = service.switchWorkspaceProfile(workspaceId);
    return new ResponseEntity<>(response, response.getStatusCode());
  }
}
