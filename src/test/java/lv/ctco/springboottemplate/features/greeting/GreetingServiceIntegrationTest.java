package lv.ctco.springboottemplate.features.greeting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lv.ctco.springboottemplate.common.MongoDbContainerTestSupport;
import lv.ctco.springboottemplate.features.todo.Todo;
import lv.ctco.springboottemplate.features.todo.TodoRepository;
import lv.ctco.springboottemplate.features.todo.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Integration test for {@link GreetingService}.
 *
 * <p>This test verifies that GreetingService correctly interacts with {@link TodoService} and
 * reflects the number of open (not completed) todos in the message.
 */
@RequiredArgsConstructor
@SpringBootTest
@Testcontainers
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class GreetingServiceIntegrationTest extends MongoDbContainerTestSupport {
  private final TodoService todoService;
  private final TodoRepository todoRepository;
  private final GreetingService greetingService;

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
    assertThat(message).contains("1 open task.");
  }
}
