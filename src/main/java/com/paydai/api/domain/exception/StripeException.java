package com.paydai.api.domain.exception;

public class StripeException extends RuntimeException {
  public StripeException(String message) { super(message);}
}
