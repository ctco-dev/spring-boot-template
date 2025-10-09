package lv.ctco.springboottemplate.features.greeting;

import lv.ctco.springboottemplate.features.todo.Todo;
import lv.ctco.springboottemplate.features.todo.TodoRepository;
import lv.ctco.springboottemplate.features.todo.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Integration test for {@link GreetingService} without Testcontainers.
 *
 * <p>This test uses a locally running MongoDB instance (expected at mongodb://localhost:27017/tododb).
 * If you prefer an embedded/in-memory Mongo, add flapdoodle dependency and remove the dynamic property below.
 */
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class GreetingServiceIntegrationTest {

  // Point Spring Data Mongo to a locally running MongoDB instead of using Testcontainers
  @DynamicPropertySource
  static void mongoProps(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", () -> "mongodb://localhost:27017/tododb");
  }

  private final TodoService todoService;
  private final TodoRepository todoRepository;
  private final GreetingService greetingService;

  GreetingServiceIntegrationTest(
      TodoService todoService, TodoRepository todoRepository, GreetingService greetingService) {
    this.todoService = todoService;
    this.todoRepository = todoRepository;
    this.greetingService = greetingService;
  }

  @BeforeEach
  void clean() {
    todoRepository.deleteAll();
  }

  @Test
  void should_include_number_of_open_todos_in_greeting() {
    // given
    todoService.createTodo("Buy bolt pistols", "For the squad", false, "marine");
    todoService.createTodo("Bless the lasgun", "With machine oil", true, "techpriest");
    todoService.createTodo("Charge plasma cell", "Don't overheat!", false, "marine");

    // when
    String message = greetingService.greet();

    // then
    assertThat(message).contains("Hello").contains("2 open tasks");
  }

  @Test
  void should_return_zero_open_tasks_message_if_none_exist() {
    // given
    todoService.createTodo("Pray to the Machine God", "Every morning", true, "techpriest");

    // when
    String message = greetingService.greet();

    // then
    assertThat(message).contains("Hello").contains("0 open tasks");
  }

  @Test
  void should_work_with_no_todos_at_all() {
    // when
    String message = greetingService.greet();

    // then
    assertThat(message).contains("Hello").contains("0 open tasks");
  }

  @Test
  void should_ignore_null_todos_or_null_completed_flags() {
    // given
    Todo manualTodo =
        new Todo(null, "Strange one", "no completed flag", false, "ghost", "ghost", null, null);

    todoRepository.saveAll(List.of(manualTodo));

    // when
    String message = greetingService.greet();

    // then
    assertThat(message).contains("1 open task");
  }
}
