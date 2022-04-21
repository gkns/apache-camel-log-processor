package com.example.flat_file_http_api.utils;

import java.time.LocalDateTime;

import com.example.flat_file_http_api.util.Utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UtilsTest {

  @Test
  public void testlocalDateTimeToDerbyTimestampFormat() {
    String utcTimestamp = "2000-01-01T17:25:49Z";
    // See https://db.apache.org/derby/docs/10.5/ref/rrefsqlj27620.html
    // supported string formats are: 1962-09-23 03:23:34.234'
    // and 1960-01-01 23:03:20. Since we don't have nanosecond precision
    // for this app, we go with the latter.
    String expectedDerbyTimestamp = "2000-01-01 17:25:49";

    // strip the 'Z' since LocalDateTime doesn't have timezone.
    utcTimestamp = utcTimestamp.substring(0, utcTimestamp.length() - 1);
    LocalDateTime timestamp = LocalDateTime.parse(utcTimestamp);
    String actualTimestamp = Utils.localDateTimeToDerbyTimestampFormat(timestamp);

    Assertions.assertEquals(expectedDerbyTimestamp, actualTimestamp);
  }
}
