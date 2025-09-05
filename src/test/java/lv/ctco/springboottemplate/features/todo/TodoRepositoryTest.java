package lv.ctco.springboottemplate.features.todo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataMongoTest
@Testcontainers
class TodoRepositoryTest {

  Instant now = Instant.now();
  List<Todo> todos =
      List.of(
          new Todo("1", "Testingsss", "test todo", false, "user1", "user2", now, now, null),
          new Todo("2", "something", "another test", true, "user2", "user3", now, now, now),
          new Todo(
              "3",
              "do the necessary",
              "live and maintain",
              false,
              "user5",
              "user1",
              now,
              now,
              null));

  @Autowired private TodoRepository todoRepository;

  @Container static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.5");

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  @BeforeEach
  void setup() {
    todoRepository.deleteAll();
    todoRepository.saveAll(todos);
  }

  @Test
  void shouldSaveAndFindTodo() {
    List<Todo> result = todoRepository.findByTitleContainingIgnoreCase("test");

    assertThat(result).hasSize(1);
    assertThat(result.get(0).title()).isEqualTo("Testingsss");
  }

  @Test
  void shouldReturnEmptyListWhenNoMatch() {
    List<Todo> result = todoRepository.findByTitleContainingIgnoreCase("aaaaaaaaaaa");
    assertThat(result).isEmpty();
  }

  // I doubt these tests need to be added, but still
  @Test
  void shouldSaveAndFindById() {
    Optional<Todo> found = todoRepository.findById(todos.get(0).id());

    assertThat(found).isNotNull();
    assertThat(found.get().title()).isEqualTo("Testingsss");
  }

  @Test
  void shouldInsertMultipleTodos() {
    todoRepository.deleteAll();

    List<Todo> bulkTodos = List.of(todos.get(0), todos.get(1));

    todoRepository.insert(bulkTodos);

    assertThat(todoRepository.count()).isEqualTo(2);
  }

  @Test
  void shouldDeleteById() {

    todoRepository.deleteById(todos.get(1).id());

    assertThat(todoRepository.existsById(todos.get(1).id())).isFalse();
    assertThat(todoRepository.existsById(todos.get(0).id())).isTrue();
  }
}
