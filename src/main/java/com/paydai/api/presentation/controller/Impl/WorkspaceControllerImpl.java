package com.paydai.api.presentation.controller.Impl;

import com.paydai.api.domain.service.WorkspaceService;
import com.paydai.api.presentation.controller.WorkspaceController;
import com.paydai.api.presentation.request.InviteRequest;
import com.paydai.api.presentation.request.PermissionRequest;
import com.paydai.api.presentation.request.WorkspaceRequest;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/workspace")
@RequiredArgsConstructor
@Tag(name = "Workspace", description = "APIs for workspaces")
public class WorkspaceControllerImpl implements WorkspaceController {
  private final WorkspaceService service;

  @Operation(
    summary = "Paydai workspace API endpoint",
    description = "POST response to show DTO"
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
  public ResponseEntity<JapiResponse> inviteToWorkspace(@RequestBody InviteRequest payload) {
    JapiResponse response = service.inviteToWorkspace(payload);
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "Paydai workspace API endpoint",
    description = "POST response to show DTO"
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
  public ResponseEntity<JapiResponse> create(@RequestBody WorkspaceRequest payload) {
    JapiResponse response = service.createWorkspace(payload);
    return new ResponseEntity<>(response, response.getStatusCode());
  }
}
