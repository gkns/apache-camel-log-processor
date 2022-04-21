package com.example.flat_file_http_api.camel.processors;

import java.util.regex.Pattern;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class RemoveNonASCIIProcessor implements Processor {
  private static final Pattern NON_ASCII_PATTERN = Pattern.compile("\\P{ASCII}");

  public static String stripNonAscii(String str) {
    return NON_ASCII_PATTERN.matcher(str).replaceAll("");
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    String line = exchange.getIn().getBody(String.class);
    line = stripNonAscii(line);
    exchange.getIn().setBody(line);
  }
}
