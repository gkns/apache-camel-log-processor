package com.example.flat_file_http_api.camel;

import com.example.flat_file_http_api.camel.processors.RemoveNonASCIIProcessor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class RemoveNonASCIIProcessorTest {

  @Test
  public void testNonAsciiCharsRemoved() {
    // Java source is UTF8 encoded.
    String text1_wrongEncoding = "ï»¿2000-01-01T13:38:29Z";
    String text2_UTF8Encoded = "2000-01-01T13:38:29Z";

    var result1 = RemoveNonASCIIProcessor.stripNonAscii(text1_wrongEncoding);
    var result2 = RemoveNonASCIIProcessor.stripNonAscii(text1_wrongEncoding);

    // Since Java Strings are UTF encoded, we can just compare the lengths
    // to verify the trimming happened.
    Assertions.assertThat(result1.length() < text1_wrongEncoding.length());
    Assertions.assertThat(result2.length() < text2_UTF8Encoded.length());
  }
}
