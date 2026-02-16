package lv.ctco.springboottemplate.features.greeting;

import org.springframework.stereotype.Service;
import lv.ctco.springboottemplate.features.todo.TodoService;

@Service 
public class GreetingService { 

    private final TodoService todoService;

    public GreetingService(TodoService todoService) {
        this.todoService = todoService;
    }

    public String greet() {
        long openTasks = todoService.getAllTodos().stream().filter(todo -> !todo.completed()).count();

        String suffix = openTasks == 1 ? "task" : "tasks";

        return "Hello from Spring! You have " + openTasks + " open " + suffix + ".";
    }
 }