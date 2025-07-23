package lv.ctco.springboottemplate.features.greeting;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lv.ctco.springboottemplate.features.todo.Todo;
import lv.ctco.springboottemplate.features.todo.TodoService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GreetingService {
  private final TodoService todoService;
  private static final String GREETING_TEMPLATE = "Hello from Spring! You have %d open %s.";

  public String greet() {
    int count = countTodos();
    String taskWord = (count == 1) ? "task" : "tasks";
    return String.format(GREETING_TEMPLATE, count, taskWord);
  }

  private int countTodos() {
    List<Todo> todoList = todoService.getAllTodos();
    return (int) todoList.stream().filter(todo -> !todo.completed()).count();
  }
}
