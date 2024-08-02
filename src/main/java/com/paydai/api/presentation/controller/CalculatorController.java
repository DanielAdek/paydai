package com.paydai.api.presentation.controller;

import com.paydai.api.presentation.request.CalcRequest;
import com.paydai.api.presentation.response.JapiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface CalculatorController {
  ResponseEntity<JapiResponse> displayCommissions(@RequestBody CalcRequest payload);
}
