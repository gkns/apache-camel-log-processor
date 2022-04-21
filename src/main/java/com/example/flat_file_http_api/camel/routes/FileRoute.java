package com.example.flat_file_http_api.camel.routes;

import static com.example.flat_file_http_api.util.Utils.CSV_DATAFORMAT_TYPE;
import static com.example.flat_file_http_api.util.Utils.FILE_ROUTE_ID;
import static com.example.flat_file_http_api.util.Utils.INPUT_FILES_DEFAULT_FOLDER_NAME;
import static com.example.flat_file_http_api.util.Utils.JDBC_AGGREGATOR_BEAN_METHOD;
import static com.example.flat_file_http_api.util.Utils.JDBC_AGGREGATOR_BEAN_URI;

import java.io.IOException;
import javax.sql.DataSource;

import com.example.flat_file_http_api.camel.predicates.BatchSizePredicate;
import com.example.flat_file_http_api.camel.processors.RemoveNonASCIIProcessor;
import com.example.flat_file_http_api.camel.strategies.ArrayListAggregationStrategy;
import com.example.flat_file_http_api.models.Session;
import com.example.flat_file_http_api.util.Utils;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FileRoute extends RouteBuilder {

  @Autowired Environment environment;

  @Autowired DataSource dataSource;

  @Value("${camel.batch.max.records}")
  private int maxRecords;

  @Value("${camel.batch.timeout}")
  private long batchTimeout;

  @Override
  public void configure() throws IOException {
    BindyDataFormat dataFormat = new BindyDataFormat();
    // The annotations in the Session class does the magic, Helping Bindy
    // make sense of the text record format.
    dataFormat.setClassType(Session.class);
    // Even though this reads CSV, Bindy doesn't mandate it is CSV -
    // it can be any delimiter separated value (XSV)
    dataFormat.setType(CSV_DATAFORMAT_TYPE);
    // We do not want to block other messages (lines in input file) in case -
    // one of the line is not properly formatted/as per spec.
    // this faulty record will be ignored. We can do custom processing too,
    // like recording them in a file.
    onException(Exception.class).handled(true);
    String inputFilesLocation = environment.getProperty(INPUT_FILES_DEFAULT_FOLDER_NAME);
    log.info("Input files location is: {}", inputFilesLocation);
    if (Utils.isEmptyDirectory(inputFilesLocation)) {
      log.error("Input files location seems to be empty. See README.md");
    }
    // charset=iso-8859-1 (Single-byte encoding), because
    // currently we don't plan to do anything with the non-single-byte characters.
    from("file://%s?charset=iso-8859-1".formatted(inputFilesLocation))
        .routeId(FILE_ROUTE_ID)
        .log(LoggingLevel.INFO, "Processing file ${file:name}")
        .threads(Runtime.getRuntime().availableProcessors())
        .split(body().tokenize("\n"))
        .streaming()
        .process(new RemoveNonASCIIProcessor())
        .unmarshal(dataFormat)
        .aggregate(constant(true), new ArrayListAggregationStrategy())
        .completionPredicate(new BatchSizePredicate(maxRecords))
        .completionTimeout(batchTimeout)
        .bean(JDBC_AGGREGATOR_BEAN_URI, JDBC_AGGREGATOR_BEAN_METHOD);
  }
}
