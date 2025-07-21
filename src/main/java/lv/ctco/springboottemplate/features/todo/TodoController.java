package lv.ctco.springboottemplate.features.todo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing {@link Todo} items.
 *
 * <p>This class defines HTTP endpoints for interacting with the application. It receives requests,
 * delegates business logic to {@link TodoService}, and returns responses.
 *
 * <p>Marked with {@link org.springframework.web.bind.annotation.RestController} and {@link
 * org.springframework.web.bind.annotation.RequestMapping}, so that Spring can map web requests to
 * methods.
 *
 * <p>📚 Learn more:
 *
 * <ul>
 *   <li><a href="https://spring.io/guides/gs/rest-service/">Spring REST Guide</a>
 *   <li><a href="https://www.baeldung.com/spring-controllers">Baeldung: Controllers</a>
 * </ul>
 */
@RestController
@RequestMapping("/api/todos")
@Tag(name = "Todo Controller", description = "Todo management endpoints")
public class TodoController {

  private final TodoService todoService;

  public TodoController(TodoService todoService) {
    this.todoService = todoService;
  }

  @GetMapping
  @Operation(summary = "Get all todos")
  public List<Todo> getAllTodos() {
    return todoService.getAllTodos();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get todo by ID")
  public ResponseEntity<Todo> getTodoById(@PathVariable String id) {
    return todoService
        .getTodoById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/search")
  @Operation(summary = "Search todos by title")
  public List<Todo> searchTodos(@RequestParam String title) {
    return todoService.searchTodos(title);
  }

  @PostMapping
  @Operation(summary = "Create new todo")
  public Todo createTodo(@RequestBody CreateTodoRequest request) {
    return todoService.createTodo(
        request.title(), request.description(), request.completed(), request.createdBy());
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update existing todo")
  public ResponseEntity<Todo> updateTodo(
      @PathVariable String id, @RequestBody UpdateTodoRequest request) {
    return todoService
        .updateTodo(
            id, request.title(), request.description(), request.completed(), request.updatedBy())
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete todo")
  public ResponseEntity<Void> deleteTodo(@PathVariable String id) {
    return todoService.deleteTodo(id)
        ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
  }

  public record CreateTodoRequest(
      String title, String description, boolean completed, String createdBy) {}

  public record UpdateTodoRequest(
      String title, String description, boolean completed, String updatedBy) {}
}
