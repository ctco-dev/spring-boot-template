package lv.ctco.springboottemplate.features.statistics;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsDetailedDto;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsSummaryDto;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsTodosDto;
import lv.ctco.springboottemplate.features.todo.Todo;
import lv.ctco.springboottemplate.features.todo.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class StatisticsServiceIntegrationTest {

  @Container static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.8");

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  private final TodoRepository todoRepository;
  private final StatisticsService statisticsService;

  StatisticsServiceIntegrationTest(
      TodoRepository todoRepository, StatisticsService statisticsService) {
    this.todoRepository = todoRepository;
    this.statisticsService = statisticsService;
  }

  @BeforeEach
  void clean() {
    todoRepository.deleteAll();
  }

  @Test
  void should_calculate_summary_without_filters() {
    // given
    todoRepository.save(
        new Todo(
            null,
            "Todo 1",
            "First",
            false,
            "user1",
            "user1",
            Instant.parse("2024-01-10T10:00:00Z"),
            Instant.parse("2024-01-10T10:00:00Z")));
    todoRepository.save(
        new Todo(
            null,
            "Todo 2",
            "Second",
            true,
            "user1",
            "user1",
            Instant.parse("2024-01-11T10:00:00Z"),
            Instant.parse("2024-01-12T10:00:00Z")));
    todoRepository.save(
        new Todo(
            null,
            "Todo 3",
            "Third",
            false,
            "user2",
            "user2",
            Instant.parse("2024-01-12T10:00:00Z"),
            Instant.parse("2024-01-12T10:00:00Z")));

    // when
    StatisticsSummaryDto summary = statisticsService.getSummary(null, null);

    // then
    assertThat(summary.totalTodos()).isEqualTo(3);
    assertThat(summary.completedTodos()).isEqualTo(1);
    assertThat(summary.pendingTodos()).isEqualTo(2);
    assertThat(summary.userStats()).containsEntry("user1", 2L).containsEntry("user2", 1L);
  }

  @Test
  void should_calculate_detailed_statistics_for_date_range() {
    // given
    todoRepository.save(
        new Todo(
            null,
            "Old todo",
            "Outside range",
            false,
            "user1",
            "user1",
            Instant.parse("2023-12-31T23:00:00Z"),
            Instant.parse("2023-12-31T23:00:00Z")));

    todoRepository.save(
        new Todo(
            null,
            "In range 1",
            "First in range",
            false,
            "user1",
            "user1",
            Instant.parse("2024-01-01T10:00:00Z"),
            Instant.parse("2024-01-01T10:00:00Z")));
    todoRepository.save(
        new Todo(
            null,
            "In range 2",
            "Completed in range",
            true,
            "user2",
            "user2",
            Instant.parse("2024-01-05T10:00:00Z"),
            Instant.parse("2024-01-06T10:00:00Z")));

    todoRepository.save(
        new Todo(
            null,
            "After range",
            "After",
            false,
            "user3",
            "user3",
            Instant.parse("2024-02-01T10:00:00Z"),
            Instant.parse("2024-02-01T10:00:00Z")));

    LocalDate from = LocalDate.parse("2024-01-01");
    LocalDate to = LocalDate.parse("2024-01-31");

    // when
    StatisticsDetailedDto detailed = statisticsService.getDetailed(from, to);

    // then
    assertThat(detailed.totalTodos()).isEqualTo(2);
    assertThat(detailed.completedTodos()).isEqualTo(1);
    assertThat(detailed.pendingTodos()).isEqualTo(1);
    assertThat(detailed.userStats()).containsEntry("user1", 1L).containsEntry("user2", 1L);

    StatisticsTodosDto todos = detailed.todos();
    assertThat(todos.completed()).hasSize(1);
    assertThat(todos.pending()).hasSize(1);
  }
}
