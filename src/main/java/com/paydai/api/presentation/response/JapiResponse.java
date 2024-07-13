package com.paydai.api.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JapiResponse {
  private Boolean status;
  private HttpStatus statusCode;
  private String message;
  private Object data;

  public static JapiResponse success(Object data) {
    return JapiResponse.builder().message("Success!").status(true).statusCode(HttpStatus.OK).data(data).build();
  }

  public static JapiResponse failed(Object data, HttpStatus code) {
    return JapiResponse.builder().message("Failed!").status(false).statusCode(code).data(data).build();
  }
}