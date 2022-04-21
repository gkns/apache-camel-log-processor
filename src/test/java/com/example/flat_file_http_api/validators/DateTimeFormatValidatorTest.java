package com.example.flat_file_http_api.validators;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.flat_file_http_api.errors.InvalidDateFormatException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DateTimeFormatValidatorTest {
  static DateTimeFormatValidator validator;

  @BeforeEach
  public void setUp() {
    validator = new DateTimeFormatValidator();
  }

  @Test
  public void testValidation() throws InvalidDateFormatException {
    validator.validISO8601DateTimeFormat("2000-01-01T17:25:49Z");
  }

  @Test
  public void testValidationThrowsOnInvalidFormat() throws InvalidDateFormatException {
    assertThrows(
        InvalidDateFormatException.class,
        () -> validator.validISO8601DateTimeFormat("2000-01-01T17:25:Z"));
  }
}
