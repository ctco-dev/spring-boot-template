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
    var todosCount = todoService.getAllTodos().stream().filter(todo -> !todo.completed()).count();
    var isPlural = todosCount != 1;
    var taskWord = isPlural ? "tasks" : "task";
    return String.format("Hello from Spring! You have %s open %s.", todosCount, taskWord);
  }
}
