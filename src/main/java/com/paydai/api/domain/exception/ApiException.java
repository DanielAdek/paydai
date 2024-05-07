package com.paydai.api.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiException {
  private final Boolean status = false;
  private String message;
  private int statusCode;
  private String error;
  private Throwable throwable;
}

