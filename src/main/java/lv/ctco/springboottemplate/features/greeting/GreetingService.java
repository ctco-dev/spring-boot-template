package lv.ctco.springboottemplate.features.greeting;

import lv.ctco.springboottemplate.features.todo.Todo;
import lv.ctco.springboottemplate.features.todo.TodoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GreetingService {
    private final TodoService todoService;

    public GreetingService(TodoService todoService) {
        this.todoService = todoService;
    }

    public String greet() {
        return String.format("Hello from Spring! You have %d open tasks.", this.countTodos());
    }

    private Integer countTodos() {
        List<Todo> todoList = this.todoService.getAllTodos();
        return (int) todoList.stream()
                .filter(todo -> Boolean.FALSE.equals(todo.completed())).count();
    }

}
