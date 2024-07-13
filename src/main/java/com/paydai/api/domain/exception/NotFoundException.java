package com.paydai.api.domain.exception;

public class NotFoundException extends RuntimeException {
  public NotFoundException(String message) {}

  public NotFoundException(String message, Throwable cause) {}
}
