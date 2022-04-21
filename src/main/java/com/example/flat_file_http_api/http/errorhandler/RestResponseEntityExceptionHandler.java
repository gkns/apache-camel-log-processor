package com.example.flat_file_http_api.http.errorhandler;

import com.example.flat_file_http_api.errors.APIException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<Object> handle(Exception ex, WebRequest request) {
    String msg = ((APIException) ex).getCustomMessage();
    return handleExceptionInternal(
        ex, msg, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }
}
