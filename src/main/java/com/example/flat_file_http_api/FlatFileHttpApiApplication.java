package com.example.flat_file_http_api;

import org.apache.camel.main.Main;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FlatFileHttpApiApplication extends Main {

  public static void main(String[] args) {
    SpringApplication.run(FlatFileHttpApiApplication.class, args);
  }
}
