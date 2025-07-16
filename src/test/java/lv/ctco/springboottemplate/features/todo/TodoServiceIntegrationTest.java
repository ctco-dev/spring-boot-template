package lv.ctco.springboottemplate.features.todo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
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
class TodoServiceIntegrationTest {

  @Container static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.8");

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  private TodoRepository todoRepository;

  private TodoService todoService;

  public TodoServiceIntegrationTest(TodoRepository todoRepository, TodoService todoService) {
    this.todoRepository = todoRepository;
    this.todoService = todoService;
  }

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
    Todo created = todoService.createTodo(title, description, completed, createdBy);
    List<Todo> allTodos = todoService.getAllTodos();

    // then
    assertThat(allTodos).hasSize(1);
    Todo todo = allTodos.get(0);

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
    assertThat(result.get(0).title()).containsIgnoringCase("milk");
  }
}
