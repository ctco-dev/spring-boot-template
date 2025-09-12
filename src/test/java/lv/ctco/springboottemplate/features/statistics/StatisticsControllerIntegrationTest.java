package lv.ctco.springboottemplate.features.statistics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lv.ctco.springboottemplate.features.todo.TodoRepository;
import lv.ctco.springboottemplate.features.todo.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class StatisticsControllerIntegrationTest {
  @Container static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.8");

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  private final StatisticsController statisticsController;
  private final TodoRepository todoRepository;
  private final TodoService todoService;

  public StatisticsControllerIntegrationTest(
      StatisticsController statisticsController,
      TodoService todoService,
      TodoRepository todoRepository) {
    this.statisticsController = statisticsController;
    this.todoRepository = todoRepository;
    this.todoService = todoService;
  }

  @BeforeEach
  void clean() {
    todoRepository.deleteAll();
  }

  @Test
  void should_filter_by_from_to() {
    // given
    todoService.createTodo("Buy bolt pistols", "For the squad", false, "marine");

    Instant fixedInstant = Instant.now().minusSeconds(60 * 60 * 24);
    MockedStatic<Instant> mockInstant = mockStatic(Instant.class);
    mockInstant.when(Instant::now).thenReturn(fixedInstant);
    todoService.createTodo("Bless the lasgun", "With machine oil", true, "techpriest");

    mockInstant.close();

    Instant fixedInstant2 = Instant.now().minusSeconds(3 * 60 * 60 * 24);
    MockedStatic<Instant> mockInstant2 = mockStatic(Instant.class);
    mockInstant2.when(Instant::now).thenReturn(fixedInstant2);
    todoService.createTodo("Charge plasma cell", "Don't overheat!", false, "marine");
    mockInstant2.close();
    // when
    ResponseEntity<Statistics> response =
        statisticsController.getStatistics(
            Optional.of(LocalDate.now().minusDays(2)),
            Optional.of(LocalDate.now().minusDays(1)),
            "summary");

    // then
    assertThat(response.getBody().totalTodos()).isEqualTo(1);
  }

  @Test
  void should_filter_by_from() {
    // given
    todoService.createTodo("Buy bolt pistols", "For the squad", false, "marine");

    Instant fixedInstant = Instant.now().minusSeconds(60 * 60 * 24);
    MockedStatic<Instant> mockInstant = mockStatic(Instant.class);
    mockInstant.when(Instant::now).thenReturn(fixedInstant);
    todoService.createTodo("Bless the lasgun", "With machine oil", true, "techpriest");

    mockInstant.close();

    Instant fixedInstant2 = Instant.now().minusSeconds(3 * 60 * 60 * 24);
    MockedStatic<Instant> mockInstant2 = mockStatic(Instant.class);
    mockInstant2.when(Instant::now).thenReturn(fixedInstant2);
    todoService.createTodo("Charge plasma cell", "Don't overheat!", false, "marine");
    mockInstant2.close();
    // when
    ResponseEntity<Statistics> response =
        statisticsController.getStatistics(
            Optional.of(LocalDate.now().minusDays(2)), Optional.empty(), "summary");

    // then
    assertThat(response.getBody().totalTodos()).isEqualTo(2);
  }

  @Test
  void should_filter_by_to() {
    // given
    todoService.createTodo("Buy bolt pistols", "For the squad", false, "marine");

    Instant fixedInstant = Instant.now().minusSeconds(60 * 60 * 24);
    MockedStatic<Instant> mockInstant = mockStatic(Instant.class);
    mockInstant.when(Instant::now).thenReturn(fixedInstant);
    todoService.createTodo("Bless the lasgun", "With machine oil", true, "techpriest");

    mockInstant.close();

    Instant fixedInstant2 = Instant.now().minusSeconds(3 * 60 * 60 * 24);
    MockedStatic<Instant> mockInstant2 = mockStatic(Instant.class);
    mockInstant2.when(Instant::now).thenReturn(fixedInstant2);
    todoService.createTodo("Charge plasma cell", "Don't overheat!", false, "marine");
    mockInstant2.close();
    // when
    ResponseEntity<Statistics> response =
        statisticsController.getStatistics(
            Optional.empty(), Optional.of(LocalDate.now().minusDays(2)), "summary");

    // then
    assertThat(response.getBody().totalTodos()).isEqualTo(1);
  }

  @Test
  void should_return_summary_fields() {
    // given
    todoService.createTodo("Buy bolt pistols", "For the squad", false, "marine");
    todoService.createTodo("Bless the lasgun", "With machine oil", true, "techpriest");
    todoService.createTodo("Charge plasma cell", "Don't overheat!", false, "marine");

    Map<String, Integer> userStats = new HashMap<>();
    userStats.put("marine", 2);
    userStats.put("techpriest", 1);

    // when
    ResponseEntity<Statistics> response =
        statisticsController.getStatistics(Optional.empty(), Optional.empty(), "summary");

    // then
    assertThat(response.getBody().totalTodos()).isEqualTo(3);
    assertThat(response.getBody().completedTodos()).isEqualTo(1);
    assertThat(response.getBody().pendingTodos()).isEqualTo(2);
    assertThat(response.getBody().userStats()).isEqualTo(userStats);
  }

  @Test
  void should_return_detailed_fields() {
    // given
    todoService.createTodo("Buy bolt pistols", "For the squad", false, "marine");
    todoService.createTodo("Bless the lasgun", "With machine oil", true, "techpriest");
    todoService.createTodo("Charge plasma cell", "Don't overheat!", false, "marine");

    Map<String, Integer> userStats = new HashMap<>();
    userStats.put("marine", 2);
    userStats.put("techpriest", 1);

    // when
    ResponseEntity<Statistics> response =
        statisticsController.getStatistics(Optional.empty(), Optional.empty(), "detailed");
    Map<String, List<StatisticsTodo>> todos = response.getBody().todos().get();

    // then
    assertThat(response.getBody().totalTodos()).isEqualTo(3);
    assertThat(response.getBody().completedTodos()).isEqualTo(1);
    assertThat(response.getBody().pendingTodos()).isEqualTo(2);
    assertThat(response.getBody().userStats()).isEqualTo(userStats);
    assertThat(todos.get("completed").size()).isEqualTo(1);
    assertThat(todos.get("pending").size()).isEqualTo(2);
    assertThat(todos.get("completed").get(0).title()).isEqualTo("Bless the lasgun");
    assertThat(todos.get("pending").get(0).title()).isEqualTo("Buy bolt pistols");
  }
}
