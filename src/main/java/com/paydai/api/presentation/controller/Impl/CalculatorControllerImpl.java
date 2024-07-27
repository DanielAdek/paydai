package com.paydai.api.presentation.controller.Impl;

import com.paydai.api.domain.service.CalculatorService;
import com.paydai.api.presentation.controller.CalculatorController;
import com.paydai.api.presentation.request.CalcRequest;
import com.paydai.api.presentation.response.JapiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/calculator")
@RequiredArgsConstructor
@Tag(name = "Commission Calculator", description = "Endpoint to display commissions")
public class CalculatorControllerImpl implements CalculatorController {
  private final CalculatorService service;

  @Operation(
    summary = "Paydai commission calculator API endpoint",
    description = "POST response to show commission DTO"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = JapiResponse.class), mediaType = "application/json")}),
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
  })
  @PostMapping("/commissions")
  @Override
  public ResponseEntity<JapiResponse> displayCommissions(@RequestBody CalcRequest payload) {
    JapiResponse response = service.displayCommissions(payload);
    return new ResponseEntity<>(response, response.getStatusCode());
  }
}
