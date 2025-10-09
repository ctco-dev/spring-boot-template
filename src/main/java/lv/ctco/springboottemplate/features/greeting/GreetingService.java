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
        var activeTodos = todoService
                .getAllTodos()
                .stream()
                .filter(todo -> !todo.completed())
                .toList();

        return "Hello from Spring! You have " + activeTodos.size() + " open tasks.";
    }
}
