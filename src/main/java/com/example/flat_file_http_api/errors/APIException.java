package com.example.flat_file_http_api.errors;

public abstract class APIException extends Exception {
  private final String errorType;
  private String customMessage;

  public APIException(String errorType, String customMessage) {
    this.errorType = errorType;
    this.customMessage = customMessage;
  }

  public String getErrorType() {
    return errorType;
  }

  public String getCustomMessage() {
    return this.customMessage;
  }
}
