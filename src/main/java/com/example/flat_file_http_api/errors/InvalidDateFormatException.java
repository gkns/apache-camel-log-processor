package com.example.flat_file_http_api.errors;

public class InvalidDateFormatException extends APIException {
  private static final String TYPE = "invalid-date-format-exception";

  public InvalidDateFormatException(String message) {
    super(TYPE, message);
  }
}
