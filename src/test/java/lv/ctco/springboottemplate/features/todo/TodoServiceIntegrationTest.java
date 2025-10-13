package lv.ctco.springboottemplate.features.todo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TodoServiceIntegrationTest {

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", () -> "mongodb://localhost:27017/tododb");
  }

  @Autowired
  private TodoRepository todoRepository;

  @Autowired
  private TodoService todoService;

  @BeforeEach
  void setup() {
    todoRepository.deleteAll();
  }

  @Test
  void shouldCreateAndRetrieveTodo() {
    // given
    String title = "Test Title";
    String description = "Test Description";
    boolean completed = false;
    String createdBy = "test-user";

    // when
    todoService.createTodo(title, description, completed, createdBy);
    List<Todo> allTodos = todoService.getAllTodos();

    // then
    assertThat(allTodos).hasSize(1);
    Todo todo = allTodos.getFirst();

    assertThat(todo.id()).isNotNull();
    assertThat(todo.title()).isEqualTo(title);
    assertThat(todo.description()).isEqualTo(description);
    assertThat(todo.completed()).isFalse();
    assertThat(todo.createdBy()).isEqualTo(createdBy);
    assertThat(todo.updatedBy()).isEqualTo(createdBy);
    assertThat(todo.createdAt()).isNotNull();
    assertThat(todo.updatedAt()).isNotNull();
  }

  @Test
  void shouldUpdateTodo() {
    // given
    Todo created = todoService.createTodo("Old", "Old", false, "creator");

    // when
    Todo updated =
        todoService
            .updateTodo(created.id(), "New Title", "New Description", true, "updater")
            .orElseThrow();

    // then
    assertThat(updated.id()).isEqualTo(created.id());
    assertThat(updated.title()).isEqualTo("New Title");
    assertThat(updated.description()).isEqualTo("New Description");
    assertThat(updated.completed()).isTrue();
    assertThat(updated.createdBy()).isEqualTo("creator");
    assertThat(updated.updatedBy()).isEqualTo("updater");
    assertThat(updated.updatedAt()).isAfter(created.updatedAt());
  }

  @Test
  void shouldDeleteTodo() {
    // given
    Todo created = todoService.createTodo("Delete Me", "Bye", false, "creator");

    // when
    boolean deleted = todoService.deleteTodo(created.id());

    // then
    assertThat(deleted).isTrue();
    assertThat(todoRepository.existsById(created.id())).isFalse();
  }

  @Test
  void shouldSearchByTitle() {
    // given
    todoService.createTodo("Buy milk", "Urgent", false, "user");
    todoService.createTodo("Buy eggs", "Important", false, "user");

    // when
    List<Todo> result = todoService.searchTodos("milk");

    // then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().title()).containsIgnoringCase("milk");
  }
}
