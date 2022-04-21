package com.example.flat_file_http_api.repositories;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;

import com.example.flat_file_http_api.models.Session;
import com.example.flat_file_http_api.util.Utils;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component("sessionDataRepositoryImpl")
@RequiredArgsConstructor
@Slf4j
public class SessionDataRepositoryImpl implements Processor {
  @PersistenceContext EntityManager entityManager;

  @Autowired CamelContext camelContext;

  @Value("${sql.insert.qeury}")
  private String batchInsertSQLQuery;

  private JdbcTemplate jdbcTemplate;
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Autowired
  public void setDataSource(final DataSource dataSource) {
    jdbcTemplate = new JdbcTemplate(dataSource);
    namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
  }

  @Transactional
  public void save(final List<Session> sessions) {
    jdbcTemplate.batchUpdate(
        batchInsertSQLQuery,
        new BatchPreparedStatementSetter() {
          @Override
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            final Session session = sessions.get(i);
            String timeStamp = session.getEventTime().format(Utils.DERBY_DATETIME_FORMATTER);
            ps.setString(1, session.getSessionId());
            ps.setString(2, session.getEmail());
            ps.setString(3, timeStamp);
          }

          @Override
          public int getBatchSize() {
            return sessions.size();
          }
        });
    log.info("Saved {} records ...", sessions.size());
  }

  public Session[] findSessionsInTimeRange(String fromDate_, String toDate_) {
    // strip trailing 'Z' since, LocalDateTime is agnostic of timezone.
    // we treat all dates as UTC in this application.
    LocalDateTime fromDate = LocalDateTime.parse(fromDate_.substring(0, fromDate_.length() - 1));
    LocalDateTime toDate = LocalDateTime.parse(toDate_.substring(0, fromDate_.length() - 1));
    // TODO: use prepared statement to avoid SQL injection.
    String dateRangeQL =
        "SELECT s FROM Session s WHERE event_time"
            + " BETWEEN TIMESTAMP('%s') AND TIMESTAMP('%s')"
                .formatted(
                    fromDate.format(Utils.DERBY_DATETIME_FORMATTER),
                    toDate.format(Utils.DERBY_DATETIME_FORMATTER));
    TypedQuery<Session> query = entityManager.createQuery(dateRangeQL, Session.class);
    // We can use Stream --> map, if we detect a problem with this later.
    return query.getResultList().toArray(new Session[0]);
  }

  // This is a NOP, so camel will pass-on the message content
  // to the next processor, if we add one, which we do in tests. ("mock:test")
  @Override
  public void process(Exchange exchange) throws Exception {}
}
