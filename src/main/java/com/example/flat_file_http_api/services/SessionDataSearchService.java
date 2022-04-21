package com.example.flat_file_http_api.services;

import com.example.flat_file_http_api.errors.InvalidDateFormatException;
import com.example.flat_file_http_api.http.requests.FindSessionsInRangeRequest;
import com.example.flat_file_http_api.models.Session;
import com.example.flat_file_http_api.repositories.SessionDataRepositoryImpl;
import com.example.flat_file_http_api.validators.DateTimeFormatValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionDataSearchService {
  @Autowired SessionDataRepositoryImpl sessionDataRepository;

  @Autowired DateTimeFormatValidator dateTimeFormatValidatorValidator;

  @PostMapping("/")
  public Session[] findSessionsInTimeRange(@RequestBody FindSessionsInRangeRequest request)
      throws InvalidDateFormatException {
    dateTimeFormatValidatorValidator.validISO8601DateTimeFormat(request.from());
    dateTimeFormatValidatorValidator.validISO8601DateTimeFormat(request.to());
    return sessionDataRepository.findSessionsInTimeRange(request.from(), request.to());
  }
}
