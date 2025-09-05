package lv.ctco.springboottemplate.features.todo_statistics;

import static lv.ctco.springboottemplate.errorhandling.ErrorMessages.TODO_STATS_DATE_RANGE_INVALID;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lv.ctco.springboottemplate.features.todo.Todo;
import lv.ctco.springboottemplate.features.todo.TodoRepository;
import lv.ctco.springboottemplate.features.todo_statistics.dto.TodoStatistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class TodoStatisticsControllerTest {

  @Container static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

  private final TestRestTemplate restTemplate;
  private final int port;
  private final TodoRepository todoRepository;

  @Autowired
  public TodoStatisticsControllerTest(
      TestRestTemplate restTemplate, @LocalServerPort int port, TodoRepository todoRepository) {
    this.restTemplate = restTemplate;
    this.port = port;
    this.todoRepository = todoRepository;
  }

  private String baseUrl() {
    return "http://localhost:" + port + "/api/statistics";
  }

  @BeforeEach
  void setUp() {
    todoRepository.deleteAll();

    List<Todo> todos =
        List.of(
            new Todo(
                null,
                "Buy milk",
                "Get 2 liters of milk",
                false,
                "alice",
                null,
                Instant.now(),
                null,
                null),
            new Todo(
                null,
                "Finish project",
                "Complete the spring boot module",
                true,
                "bob",
                "charlie",
                Instant.now().minus(3, ChronoUnit.DAYS),
                Instant.now().minus(1, ChronoUnit.DAYS),
                Instant.now().minus(1, ChronoUnit.DAYS)),
            new Todo(
                null,
                "Read book",
                "Read 'Effective Java'",
                false,
                "alice",
                null,
                Instant.now().minus(5, ChronoUnit.DAYS),
                null,
                null));

    todoRepository.saveAll(todos);
  }

  @Test
  void shouldReturnStatistics() {
    Instant from = Instant.now().minus(2, ChronoUnit.DAYS);
    Instant to = Instant.now();

    String url =
        String.format("%s?format=summary&from=%s&to=%s", baseUrl(), from.toString(), to.toString());

    ResponseEntity<TodoStatistics> response = restTemplate.getForEntity(url, TodoStatistics.class);

    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    TodoStatistics stats = response.getBody();
    assertThat(stats).isNotNull();
  }

  @Test
  void shouldReturnStatisticsWithMissingRequestParams() {
    Instant from = Instant.now().minus(2, ChronoUnit.DAYS);

    String url = String.format("%s?from=%s", baseUrl(), from.toString());

    ResponseEntity<TodoStatistics> response = restTemplate.getForEntity(url, TodoStatistics.class);

    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    TodoStatistics stats = response.getBody();
    assertThat(stats).isNotNull();
  }

  @Test
  void shouldIgnoreFormatCase() {
    Instant from = Instant.now().minus(2, ChronoUnit.DAYS);
    Instant to = Instant.now();

    String url =
        String.format(
            "%s?format=DETAIled&from=%s&to=%s", baseUrl(), from.toString(), to.toString());

    ResponseEntity<TodoStatistics> response = restTemplate.getForEntity(url, TodoStatistics.class);

    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    TodoStatistics stats = response.getBody();
    assertThat(stats).isNotNull();
  }

  @Test
  void shouldHandleEmptyResponse() {
    Instant from = Instant.now().minus(101, ChronoUnit.DAYS);
    Instant to = Instant.now().minus(100, ChronoUnit.DAYS);

    String url = String.format("%s?from=%s&to=%s", baseUrl(), from.toString(), to.toString());

    ResponseEntity<TodoStatistics> response = restTemplate.getForEntity(url, TodoStatistics.class);

    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    TodoStatistics stats = response.getBody();
    assertThat(stats).isNotNull();
    assertThat(stats.getTotalTodos()).isEqualTo(0);
  }

  @Test
  void shouldReturnErrorIfMissingParams() {

    ResponseEntity<String> response = restTemplate.getForEntity(baseUrl(), String.class);

    assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    assertThat(response.getBody())
        .withFailMessage(
            "Expected response body to contain error message: %s", TODO_STATS_DATE_RANGE_INVALID)
        .contains(TODO_STATS_DATE_RANGE_INVALID);
  }

  @Test
  void shouldReturnErrorIfInvalidFormat() {
    Instant from = Instant.now().minus(2, ChronoUnit.DAYS);
    Instant to = Instant.now();

    String url =
        String.format(
            "%s?format=deta1led&from=%s&to=%s", baseUrl(), from.toString(), to.toString());

    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

    assertThat(response.getStatusCode().is4xxClientError()).isTrue();
  }
}
