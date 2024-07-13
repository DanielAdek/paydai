package com.paydai.api.presentation.controller.Impl;

import com.paydai.api.domain.service.PermissionService;
import com.paydai.api.presentation.controller.PermissionController;
import com.paydai.api.presentation.request.PermissionRequest;
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
@RequestMapping(path = "/api/v1/permission")
@RequiredArgsConstructor
@Tag(name = "Permission", description = "APIs for permission")
public class PermissionControllerImpl implements PermissionController {
  private final PermissionService service;
  @Operation(
    summary = "Paydai permissions API endpoint",
    description = "POST response to show DTO"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = JapiResponse.class), mediaType = "application/json")}),
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
  })
  @Override
  @GetMapping(path = "/open")
  public ResponseEntity<JapiResponse> getPermission() {
    JapiResponse response = service.getPermissions();
    return new ResponseEntity<>(response, response.getStatusCode());
  }

  @Operation(
    summary = "Paydai permission API endpoint (***don't use****)",
    description = "POST response to show DTO"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = JapiResponse.class), mediaType = "application/json")}),
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
  })
  @Override
  @PostMapping(path = "/create")
  public ResponseEntity<JapiResponse> create(@RequestBody PermissionRequest payload) {
    JapiResponse response = service.create(payload);
    return new ResponseEntity<>(response, response.getStatusCode());
  }
}
