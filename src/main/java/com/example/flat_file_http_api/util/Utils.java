package com.example.flat_file_http_api.util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// This could be a bean, If this grows to have non-static methods too.
// (further note, beans are by default singleton)
public class Utils {
  public static final String FILE_ROUTE_ID = "textFileRecordRouter";
  public static final String JDBC_ROUTE_URI = "jdbc:dataSource";
  public static final String JDBC_AGGREGATOR_BEAN_URI = "sessionDataRepositoryImpl";
  public static final String CAMEL_AGGREGATOR_REF = "arrayListAggregator";
  public static final String JDBC_AGGREGATOR_BEAN_METHOD = "save";
  public static final String CSV_DATAFORMAT_TYPE = "Csv";
  public static final String INPUT_FILES_DEFAULT_FOLDER_NAME = "input_files_folder";
  public static final String DERBY_TIMESTAMP_FORMATTER_PATTERN = "yyyy-MM-dd H:mm:ss";
  public static final String UTC_TIMESTAMP_FORMATTER_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

  public static final DateTimeFormatter DERBY_DATETIME_FORMATTER =
      DateTimeFormatter.ofPattern(DERBY_TIMESTAMP_FORMATTER_PATTERN);
  public static final DateTimeFormatter UTC_TIMESTAMP_FORMATTER =
      DateTimeFormatter.ofPattern(UTC_TIMESTAMP_FORMATTER_PATTERN);

  public static boolean isEmptyDirectory(String pathStr) throws IOException {
    Path path = Paths.get(pathStr);
    if (Files.isDirectory(path)) {
      try (DirectoryStream<Path> directory = Files.newDirectoryStream(path)) {
        return !directory.iterator().hasNext();
      }
    }
    return false;
  }

  public static String localDateTimeToDerbyTimestampFormat(LocalDateTime date) {
    return date.format(DERBY_DATETIME_FORMATTER);
  }
}
