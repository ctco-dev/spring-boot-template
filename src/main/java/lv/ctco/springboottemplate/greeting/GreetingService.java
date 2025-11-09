package lv.ctco.springboottemplate.greeting;

import lv.ctco.springboottemplate.features.todo.TodoService;
import org.springframework.stereotype.Service;

@Service
public class GreetingService {
  private final TodoService todoService;

  public GreetingService(TodoService todoService) {
    this.todoService = todoService;
  }

  public String greet() {
    long pendingCount = todoService.getAllTodos().stream().filter(t -> !t.completed()).count();

    return "Hello from Spring! You have " + pendingCount + " open tasks.";
  }
}
