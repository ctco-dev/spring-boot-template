package lv.ctco.springboottemplate.features.statistics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import lv.ctco.springboottemplate.features.todo.TodoRepository;
import lv.ctco.springboottemplate.features.todo.TodoService;
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

  private final TodoService todoService;
  private final TodoRepository todoRepository;
  private final StatisticsService statisticsService;

  StatisticsServiceIntegrationTest(
      TodoService todoService, TodoRepository todoRepository, StatisticsService statisticsService) {
    this.todoService = todoService;
    this.todoRepository = todoRepository;
    this.statisticsService = statisticsService;
  }

  @BeforeEach
  void clean() {
    todoRepository.deleteAll();
  }

  @Test
  void shouldGetSummaryStatisticsForAllTodos() {
    // given
    todoService.createTodo("Task 1", "Description 1", true, "user1");
    todoService.createTodo("Task 2", "Description 2", false, "user1");
    todoService.createTodo("Task 3", "Description 3", true, "user2");

    // when
    GetStatisticsParams params =
        new GetStatisticsParams(Optional.empty(), Optional.empty(), StatisticsFormat.SUMMARY);
    Statistics statistics = statisticsService.getStatistics(params);

    // then
    assertThat(statistics.totalTodos()).isEqualTo(3);
    assertThat(statistics.completedTodos()).isEqualTo(2);
    assertThat(statistics.pendingTodos()).isEqualTo(1);
    assertThat(statistics.userStats())
        .containsExactlyInAnyOrderEntriesOf(Map.of("user1", 2L, "user2", 1L));
    assertThat(statistics.todos()).isEmpty();
  }

  @Test
  void shouldGetDetailedStatisticsWithTodoLists() {
    // given
    todoService.createTodo("Task 1", "Description 1", true, "user1");
    todoService.createTodo("Task 2", "Description 2", false, "user1");

    // when
    GetStatisticsParams params =
        new GetStatisticsParams(Optional.empty(), Optional.empty(), StatisticsFormat.DETAILED);
    Statistics statistics = statisticsService.getStatistics(params);

    // then
    assertThat(statistics.totalTodos()).isEqualTo(2);
    assertThat(statistics.completedTodos()).isEqualTo(1);
    assertThat(statistics.pendingTodos()).isEqualTo(1);
    assertThat(statistics.todos()).isPresent();

    TodoStatistics todoStats = statistics.todos().get();
    assertThat(todoStats.completed()).hasSize(1);
    assertThat(todoStats.pending()).hasSize(1);
    assertThat(todoStats.completed().get(0).title()).isEqualTo("Task 1");
    assertThat(todoStats.pending().get(0).title()).isEqualTo("Task 2");
  }

  @Test
  void shouldFilterByDateRange() {
    // given
    LocalDate today = LocalDate.now();
    LocalDate yesterday = today.minusDays(1);

    todoService.createTodo(
        "Old Task",
        "Old",
        true,
        "user1",
        yesterday.atStartOfDay().plusHours(10).toInstant(java.time.ZoneOffset.UTC));
    todoService.createTodo(
        "New Task",
        "New",
        false,
        "user1",
        today.atStartOfDay().plusHours(10).toInstant(java.time.ZoneOffset.UTC));

    // when
    GetStatisticsParams params =
        new GetStatisticsParams(Optional.of(today), Optional.empty(), StatisticsFormat.SUMMARY);
    Statistics statistics = statisticsService.getStatistics(params);

    // then
    assertThat(statistics.totalTodos()).isEqualTo(1);
    assertThat(statistics.completedTodos()).isEqualTo(0);
    assertThat(statistics.pendingTodos()).isEqualTo(1);
  }

  @Test
  void shouldFilterByBothDateRange() {
    // given
    LocalDate today = LocalDate.now();
    LocalDate yesterday = today.minusDays(1);
    LocalDate tomorrow = today.plusDays(1);

    todoService.createTodo(
        "Yesterday's Task",
        "Yesterday",
        true,
        "user1",
        yesterday.atStartOfDay().plusHours(10).toInstant(java.time.ZoneOffset.UTC));
    todoService.createTodo(
        "Today's Task",
        "Today",
        false,
        "user1",
        today.atStartOfDay().plusHours(10).toInstant(java.time.ZoneOffset.UTC));
    todoService.createTodo(
        "Tomorrow's Task",
        "Tomorrow",
        true,
        "user1",
        tomorrow.atStartOfDay().plusHours(10).toInstant(java.time.ZoneOffset.UTC));

    // when
    GetStatisticsParams params =
        new GetStatisticsParams(Optional.of(today), Optional.of(today), StatisticsFormat.SUMMARY);
    Statistics statistics = statisticsService.getStatistics(params);

    // then
    assertThat(statistics.totalTodos()).isEqualTo(1);
    assertThat(statistics.completedTodos()).isEqualTo(0);
    assertThat(statistics.pendingTodos()).isEqualTo(1);
  }

  @Test
  void shouldValidateDateRangeAndThrowException() {
    // given
    LocalDate today = LocalDate.now();
    LocalDate yesterday = today.minusDays(1);

    // when & then
    GetStatisticsParams params =
        new GetStatisticsParams(
            Optional.of(today), Optional.of(yesterday), StatisticsFormat.SUMMARY);

    assertThatThrownBy(() -> statisticsService.getStatistics(params))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("From date must be before or equal to to date");
  }

  @Test
  void shouldAllowEqualDates() {
    // given
    LocalDate today = LocalDate.now();
    todoService.createTodo(
        "Today's Task",
        "Today",
        false,
        "user1",
        today.atStartOfDay().plusHours(10).toInstant(java.time.ZoneOffset.UTC));

    // when
    GetStatisticsParams params =
        new GetStatisticsParams(Optional.of(today), Optional.of(today), StatisticsFormat.SUMMARY);
    Statistics statistics = statisticsService.getStatistics(params);

    // then
    assertThat(statistics.totalTodos()).isEqualTo(1);
    assertThat(statistics.pendingTodos()).isEqualTo(1);
  }

  @Test
  void shouldHandleEmptyDatabase() {
    // when
    GetStatisticsParams params =
        new GetStatisticsParams(Optional.empty(), Optional.empty(), StatisticsFormat.SUMMARY);
    Statistics statistics = statisticsService.getStatistics(params);

    // then
    assertThat(statistics.totalTodos()).isEqualTo(0);
    assertThat(statistics.completedTodos()).isEqualTo(0);
    assertThat(statistics.pendingTodos()).isEqualTo(0);
    assertThat(statistics.userStats()).isEmpty();
  }

  @Test
  void shouldCalculateUserStatisticsCorrectly() {
    // given
    todoService.createTodo("Task 1", "Description 1", true, "user1");
    todoService.createTodo("Task 2", "Description 2", false, "user1");
    todoService.createTodo("Task 3", "Description 3", true, "user2");
    todoService.createTodo("Task 4", "Description 4", false, "user2");
    todoService.createTodo("Task 5", "Description 5", true, "user3");

    // when
    GetStatisticsParams params =
        new GetStatisticsParams(Optional.empty(), Optional.empty(), StatisticsFormat.SUMMARY);
    Statistics statistics = statisticsService.getStatistics(params);

    // then
    assertThat(statistics.totalTodos()).isEqualTo(5);
    assertThat(statistics.completedTodos()).isEqualTo(3);
    assertThat(statistics.pendingTodos()).isEqualTo(2);
    assertThat(statistics.userStats())
        .containsExactlyInAnyOrderEntriesOf(Map.of("user1", 2L, "user2", 2L, "user3", 1L));
  }

  @Test
  void shouldHandleDetailedStatisticsWithEmptyResults() {
    // given
    // No todos created

    // when
    GetStatisticsParams params =
        new GetStatisticsParams(Optional.empty(), Optional.empty(), StatisticsFormat.DETAILED);
    Statistics statistics = statisticsService.getStatistics(params);

    // then
    assertThat(statistics.totalTodos()).isEqualTo(0);
    assertThat(statistics.todos()).isPresent();

    TodoStatistics todoStats = statistics.todos().get();
    assertThat(todoStats.completed()).isEmpty();
    assertThat(todoStats.pending()).isEmpty();
  }

  @Test
  void shouldHandleDetailedStatisticsWithOnlyCompletedTodos() {
    // given
    todoService.createTodo("Task 1", "Description 1", true, "user1");
    todoService.createTodo("Task 2", "Description 2", true, "user1");

    // when
    GetStatisticsParams params =
        new GetStatisticsParams(Optional.empty(), Optional.empty(), StatisticsFormat.DETAILED);
    Statistics statistics = statisticsService.getStatistics(params);

    // then
    assertThat(statistics.totalTodos()).isEqualTo(2);
    assertThat(statistics.completedTodos()).isEqualTo(2);
    assertThat(statistics.pendingTodos()).isEqualTo(0);
    assertThat(statistics.todos()).isPresent();

    TodoStatistics todoStats = statistics.todos().get();
    assertThat(todoStats.completed()).hasSize(2);
    assertThat(todoStats.pending()).isEmpty();
  }

  @Test
  void shouldHandleDetailedStatisticsWithOnlyPendingTodos() {
    // given
    todoService.createTodo("Task 1", "Description 1", false, "user1");
    todoService.createTodo("Task 2", "Description 2", false, "user1");

    // when
    GetStatisticsParams params =
        new GetStatisticsParams(Optional.empty(), Optional.empty(), StatisticsFormat.DETAILED);
    Statistics statistics = statisticsService.getStatistics(params);

    // then
    assertThat(statistics.totalTodos()).isEqualTo(2);
    assertThat(statistics.completedTodos()).isEqualTo(0);
    assertThat(statistics.pendingTodos()).isEqualTo(2);
    assertThat(statistics.todos()).isPresent();

    TodoStatistics todoStats = statistics.todos().get();
    assertThat(todoStats.completed()).isEmpty();
    assertThat(todoStats.pending()).hasSize(2);
  }
}
