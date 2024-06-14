package com.paydai.api.presentation.controller.Impl;

import com.paydai.api.domain.service.RoleService;
import com.paydai.api.presentation.controller.RoleController;
import com.paydai.api.presentation.request.RoleRequest;
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
@RequestMapping(path = "/api/v1/role")
@RequiredArgsConstructor
@Tag(name = "Role", description = "APIs for roles")
public class RoleControllerImpl implements RoleController {
  private final RoleService service;

  @Operation(
    summary = "Paydai role API endpoint",
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
  @GetMapping
  @Override
  public ResponseEntity<JapiResponse> getWorkspaceRoles(@RequestParam UUID workspaceId) {
    JapiResponse response = service.getRolesByWorkspace(workspaceId);
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "Paydai role API endpoint",
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
  @PostMapping(path = "/create")
  @Override
  public ResponseEntity<JapiResponse> create(@RequestBody RoleRequest payload) {
    JapiResponse response = service.createRole(payload);
    return new ResponseEntity<>(response, response.getStatusCode());
  }
}
