package com.example.flat_file_http_api.validators;

import com.example.flat_file_http_api.errors.InvalidDateFormatException;
import com.example.flat_file_http_api.util.Utils;

import org.springframework.stereotype.Component;

@Component
public class DateTimeFormatValidator {
  public void validISO8601DateTimeFormat(String timestamp) throws InvalidDateFormatException {
    try {
      Utils.UTC_TIMESTAMP_FORMATTER.parse(timestamp);
    } catch (java.time.format.DateTimeParseException e) {
      throw new InvalidDateFormatException("Date is not a valid ISO8601 representation");
    }
  }
}
