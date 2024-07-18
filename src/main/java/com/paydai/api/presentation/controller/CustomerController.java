package com.paydai.api.presentation.controller;

import com.paydai.api.domain.model.CustomerModel;
import com.paydai.api.presentation.request.CustomerRequest;
import com.paydai.api.presentation.response.JapiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface CustomerController {
  ResponseEntity<JapiResponse> create(@RequestBody CustomerRequest payload);
  ResponseEntity<JapiResponse> getCustomers();
  ResponseEntity<JapiResponse> getOneCustomer(@RequestParam UUID customerId);
}
