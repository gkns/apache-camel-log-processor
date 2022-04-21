package com.example.flat_file_http_api.http.requests;

public record FindSessionsInRangeRequest(String filename, String from, String to) {
}
