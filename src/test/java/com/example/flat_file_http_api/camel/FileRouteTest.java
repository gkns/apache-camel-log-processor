package com.example.flat_file_http_api.camel;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.flat_file_http_api.camel.routes.FileRoute;
import com.example.flat_file_http_api.models.Session;
import com.example.flat_file_http_api.repositories.SessionDataRepositoryImpl;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@CamelSpringBootTest
@EnableAutoConfiguration
@SpringBootTest(
    properties = {"camel.springboot.name=customName"},
    classes = {
      FileRoute.class,
      SessionDataRepositoryImpl.class,
    })
public class FileRouteTest {

  @Autowired Environment environment;

  @EndpointInject("mock:test")
  MockEndpoint mockEndpoint;

  @Autowired CamelContext context;
  @Autowired private ProducerTemplate producerTemplate;
  private String endpointURI;
  private String sampleMsgBody =
      "2000-01-01T17:25:49Z ddedric_strosin@adams.co.uk"
          + " dfad33e7-f734-4f70-af29-c42f2b467142\n";

  @Test
  public void shouldAutowireProducerTemplate() {
    assertNotNull(producerTemplate);
  }

  @Test
  public void shouldNotThrowOnInvalidInputMessage() throws InterruptedException {
    endpointURI = context.getRoute("textFileRecordRouter").getEndpoint().getEndpointUri();
    mockEndpoint.setExpectedMessageCount(0);
    producerTemplate.sendBody(endpointURI, "");

    mockEndpoint.assertIsSatisfied();
  }

  @Test
  public void shouldTransformLinesToArrayListOfSessions() throws Exception {
    // Replace the destination camel endpoint to mock endpoint, to inspect the message
    AdviceWith.adviceWith(
        context, "textFileRecordRouter", a -> a.weaveById("aggregate*").after().to("mock:test"));

    endpointURI = context.getRoute("textFileRecordRouter").getEndpoint().getEndpointUri();

    producerTemplate.sendBody(endpointURI, sampleMsgBody);

    List<Session> expectedBody = new ArrayList<>();
    expectedBody.add(
        new Session(
            "dfad33e7-f734-4f70-af29-c42f2b467142",
            LocalDateTime.parse("2000-01-01T17:25:49"),
            "ddedric_strosin@adams.co.uk",
            null));
    mockEndpoint.expectedBodiesReceived(expectedBody);
    mockEndpoint.assertIsSatisfied();
  }
}
