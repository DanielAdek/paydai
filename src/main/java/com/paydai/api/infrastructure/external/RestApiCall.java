package com.paydai.api.infrastructure.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paydai.api.domain.exception.ApiRequestException;
import com.paydai.api.presentation.dto.auth.AuthModelDto;
import com.paydai.api.presentation.response.JapiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RestApiCall {
  private final RestApiConfigBean apiCall;
  private final ObjectMapper objectMapper;

  private HttpHeaders writeHeaders(String token, String key) {
    HttpHeaders headers = new HttpHeaders();

    headers.setContentType(MediaType.APPLICATION_JSON);

    headers.set("Content-Encoding", "UTF-8");

    if (key != null) {
      headers.setBasicAuth(key);
    }

    if (token != null) {
      headers.setBearerAuth(token);
    }

    return headers;
  }

  public JapiResponse reachOut(HttpMethod method, Object payload, String endpoint, String key) {
    try {
      HttpHeaders headers = writeHeaders(null, key);

      String mapped = objectMapper.writeValueAsString(payload);

      HttpEntity<String> entity = new HttpEntity<>(mapped, headers);

      // Use the exchange method with the correct parameters
      ResponseEntity<JapiResponse> response = apiCall.restTemplate().exchange(endpoint, method, entity, JapiResponse.class);

      return JapiResponse.success(Objects.requireNonNull(response.getBody()));
    } catch (Exception e) {
      System.out.println(e);
      throw new ApiRequestException(e.getMessage(), e);
    }
  }

  public JapiResponse reachOutToAuth(HttpMethod method, Object payload, String endpoint, String jwt) {
    try {
     HttpHeaders headers = writeHeaders(jwt, null);

      String mapped = objectMapper.writeValueAsString(payload);

      HttpEntity<String> entity = new HttpEntity<>(mapped, headers);

      // Use the exchange method with the correct parameters
      ResponseEntity<AuthModelDto> response = apiCall.restTemplate().exchange(endpoint, method, entity, AuthModelDto.class);

      return JapiResponse.success(Objects.requireNonNull(response.getBody()));
    } catch (Exception e) {
      System.out.println(e);
      return JapiResponse.builder().message("Failed!").statusCode(HttpStatus.INTERNAL_SERVER_ERROR).data(e).build();
    }
  }
}
