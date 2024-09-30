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
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/workspace")
@RequiredArgsConstructor
@Tag(name = "Workspaces", description = "APIs for workspaces")
public class WorkspaceControllerImpl implements WorkspaceController {
  private final WorkspaceService service;

  @Operation(
    summary = "Paydai workspace API endpoint",
    description = "GET response to show DTO"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = JapiResponse.class), mediaType = "application/json")}),
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
  })
  @SecurityRequirements({
    @SecurityRequirement(name = "Authorization", scopes = {"read", "write"})
  })
  @GetMapping
  @Override
  public ResponseEntity<JapiResponse> getWorkspace() {
    JapiResponse response = service.getWorkspace();
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "Paydai workspace API endpoint",
    description = "GET response to show DTO"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = JapiResponse.class), mediaType = "application/json")}),
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
  })
  @SecurityRequirements({
    @SecurityRequirement(name = "Authorization", scopes = {"read", "write"})
  })
  @GetMapping("/sales-rep")
  @Override
  public ResponseEntity<JapiResponse> getSalesRepWorkspaces() {
    JapiResponse response = service.getSalesRepWorkspaces();
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "A merchant workspace sales reps API endpoint",
    description = "GET response to show DTO"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = JapiResponse.class), mediaType = "application/json")}),
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
  })
  @SecurityRequirements({
    @SecurityRequirement(name = "Authorization", scopes = {"read", "write"})
  })
  @GetMapping("/sales-reps")
  @Override
  public ResponseEntity<JapiResponse> getWorkspaceSalesReps(@RequestParam UUID workspaceId, @RequestParam Optional<UUID> roleId) {
    JapiResponse response = service.getWorkspaceSalesReps(workspaceId, roleId);
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "A merchant workspace teams reps API endpoint",
    description = "GET response to show DTO"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = JapiResponse.class), mediaType = "application/json")}),
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
  })
  @SecurityRequirements({
    @SecurityRequirement(name = "Authorization", scopes = {"read", "write"})
  })
  @GetMapping("/teams")
  @Override
  public ResponseEntity<JapiResponse> getWorkspaceTeams(@RequestParam UUID workspaceId) {
    JapiResponse response = service.getWorkspaceTeams(workspaceId);
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "A merchant manager workspace teams reps API endpoint",
    description = "GET response to show DTO"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = JapiResponse.class), mediaType = "application/json")}),
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
  })
  @SecurityRequirements({
    @SecurityRequirement(name = "Authorization", scopes = {"read", "write"})
  })
  @GetMapping("/manager/teams")
  @Override
  public ResponseEntity<JapiResponse> getManagerTeamMembers(UUID workspaceId) {
    JapiResponse response = service.getManagerTeamMembers(workspaceId);
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "A merchant remove workspace rep API endpoint",
    description = "GET response to show DTO"
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
  @DeleteMapping("remove/sales-rep")
  public ResponseEntity<JapiResponse> removeWorkspaceMember(@RequestParam UUID userId, @RequestParam UUID workspaceId) {
    JapiResponse response = service.removeWorkspaceMember(userId, workspaceId);
    return new ResponseEntity<>(response, response.getStatusCode());
  }
}
