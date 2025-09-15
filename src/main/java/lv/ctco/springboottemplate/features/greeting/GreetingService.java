package lv.ctco.springboottemplate.features.greeting;

import lv.ctco.springboottemplate.features.todo.TodoService;
import org.springframework.stereotype.Service;

@Service
public class GreetingService {
  private final TodoService todoService;

  public GreetingService(TodoService todoService) {
    this.todoService = todoService;
  }

  public String greet() {
    var openTasksCount =
        todoService.getAllTodos().stream().filter(todo -> !todo.completed()).count();
    return String.format(
        "Hello from Spring! You have %d open %s.",
        openTasksCount, openTasksCount == 1 ? "task" : "tasks");
  }
}
