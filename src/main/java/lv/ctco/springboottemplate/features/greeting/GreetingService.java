package lv.ctco.springboottemplate.features.greeting;

import lv.ctco.springboottemplate.features.todo.TodoService;
import org.springframework.stereotype.Service;

/** Service layer for greeting messages */
@Service
public class GreetingService {
  private final TodoService todoService;

  private GreetingService(TodoService todoService) {
    this.todoService = todoService;
  }

  public String greet() {
    long pendingItemsCount =
        todoService.getAllTodos().stream().filter(todo -> !todo.completed()).count();

    return "Hello from Spring! You have " + pendingItemsCount + " open tasks.";
  }
}
