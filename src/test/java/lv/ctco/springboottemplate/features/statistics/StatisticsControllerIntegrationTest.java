package lv.ctco.springboottemplate.features.statistics;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsSummaryDto;
import lv.ctco.springboottemplate.features.todo.Todo;
import lv.ctco.springboottemplate.features.todo.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class StatisticsControllerIntegrationTest {

  @Container static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.8");

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private TodoRepository todoRepository;

  @BeforeEach
  void setup() {
    todoRepository.deleteAll();
    todoRepository.save(
        new Todo(
            null,
            "Sample",
            "Sample desc",
            false,
            "user1",
            "user1",
            Instant.parse("2024-01-10T10:00:00Z"),
            Instant.parse("2024-01-10T10:00:00Z")));
  }

  @Test
  void should_return_summary_statistics_via_http() {
    String url = "http://localhost:" + port + "/api/statistics?format=summary";

    ResponseEntity<StatisticsSummaryDto> response =
        restTemplate.getForEntity(url, StatisticsSummaryDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    StatisticsSummaryDto body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.totalTodos()).isEqualTo(1);
    assertThat(body.pendingTodos()).isEqualTo(1);
    assertThat(body.completedTodos()).isZero();
  }

  @Test
  void should_return_bad_request_for_invalid_date() {
    String url = "http://localhost:" + port + "/api/statistics?from=2024-99-99";

    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }
}
