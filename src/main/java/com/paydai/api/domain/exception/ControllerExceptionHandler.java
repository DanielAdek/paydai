package com.paydai.api.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

  @ExceptionHandler(value = {ApiRequestException.class})
  public ResponseEntity<ApiException> handleApiRequestException(ApiRequestException cause) {
    ApiException apiException = ApiException.builder()
        .message(cause.getMessage())
        .error("Bad Request Exception")
        .throwable(cause.getCause())
        .statusCode(400)
        .build();
    return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
  };

  @ExceptionHandler(value = {NotFoundException.class})
  public ResponseEntity<ApiException> handleNotFoundException(NotFoundException cause) {
    ApiException exception = ApiException.builder()
        .message(cause.getMessage())
        .error("Not Found Exception")
        .throwable(cause.getCause())
        .statusCode(404)
        .build();
    return new ResponseEntity<>(exception, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(value = {InternalServerException.class})
  public ResponseEntity<ApiException> handleServerException(InternalServerException cause) {
    ApiException exception = ApiException.builder()
        .message("Internal Server Error")
        .statusCode(500)
        .error(cause.getMessage())
        .throwable(cause.getCause())
        .build();
    return new ResponseEntity<>(exception, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = {ConflictException.class})
  public ResponseEntity<ApiException> handleConflictException(ConflictException cause) {
    ApiException exception = ApiException.builder()
        .message(cause.getMessage())
        .statusCode(409)
        .error("Duplicate Key violate unique constraint")
        .throwable(cause.getCause())
        .build();
    return new ResponseEntity<>(exception, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(value = {ForbiddenException.class})
  public ResponseEntity<ApiException> handleForbiddenException(ForbiddenException cause) {
    ApiException exception = ApiException.builder()
        .message("Access Denied")
        .statusCode(403)
        .error(cause.getMessage())
        .throwable(cause.getCause())
        .build();
    return new ResponseEntity<>(exception, HttpStatus.FORBIDDEN);
  }
}
