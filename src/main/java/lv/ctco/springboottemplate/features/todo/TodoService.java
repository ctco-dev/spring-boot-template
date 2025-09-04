package lv.ctco.springboottemplate.features.todo;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Service layer for managing {@link Todo} items.
 *
 * <p>This class encapsulates business logic and acts as a bridge between the controller (HTTP
 * layer) and the repository (data access layer).
 *
 * <p>Spring automatically detects this class as a service due to the {@link
 * org.springframework.stereotype.Service} annotation and registers it as a <b>singleton bean</b> in
 * the application context.
 *
 * <p>Dependencies are injected via constructor (constructor-based Dependency Injection), which
 * promotes immutability and testability.
 *
 * <p>📚 Learn more:
 *
 * <ul>
 *   <li><a href="https://spring.io/guides/gs/rest-service/">Spring REST Guide</a>
 *   <li><a href="https://www.baeldung.com/spring-component-repository-service">Baeldung: Service
 *       Component Repo Layers</a>
 *   <li><a href="https://docs.spring.io/spring-framework/reference/core/beans/">Spring Beans &
 *       DI</a>
 * </ul>
 */
@Service
public class TodoService {
  private final TodoRepository todoRepository;

  public TodoService(TodoRepository todoRepository) {
    this.todoRepository = todoRepository;
  }

  public List<Todo> getAllTodos() {
    return todoRepository.findAll();
  }

  public Optional<Todo> getTodoById(String id) {
    return todoRepository.findById(id);
  }

  public List<Todo> searchTodos(String title) {
    return todoRepository.findByTitleContainingIgnoreCase(title);
  }

  public Todo createTodo(String title, String description, boolean completed, String createdBy) {
    var now = Instant.now();
    var todo = new Todo(null, title, description, completed, createdBy, createdBy, now, now);
    return todoRepository.save(todo);
  }

  public Todo createTodo(
      String title, String description, boolean completed, String createdBy, Instant createdAt) {
    var todo =
        new Todo(null, title, description, completed, createdBy, createdBy, createdAt, createdAt);
    return todoRepository.save(todo);
  }

  public Optional<Todo> updateTodo(
      String id, String title, String description, boolean completed, String updatedBy) {
    return todoRepository
        .findById(id)
        .map(
            existingTodo -> {
              var updatedTodo =
                  new Todo(
                      existingTodo.id(),
                      title,
                      description,
                      completed,
                      existingTodo.createdBy(),
                      updatedBy,
                      existingTodo.createdAt(),
                      Instant.now());
              return todoRepository.save(updatedTodo);
            });
  }

  public boolean deleteTodo(String id) {
    if (todoRepository.existsById(id)) {
      todoRepository.deleteById(id);
      return true;
    }
    return false;
  }
}
