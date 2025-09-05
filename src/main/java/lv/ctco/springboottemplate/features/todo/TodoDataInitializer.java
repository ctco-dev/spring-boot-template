package lv.ctco.springboottemplate.features.todo;

import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TodoDataInitializer {

  private static final Logger log = LoggerFactory.getLogger(TodoDataInitializer.class);

  @Bean
  CommandLineRunner initDatabase(TodoRepository todoRepository) {
    return args -> {
      todoRepository.deleteAll(); // Clear existing data

      var now = Instant.now();
      var todos =
          List.of(
              new Todo(
                  null,
                  "Buy groceries",
                  "Milk, eggs, bread",
                  false,
                  "user3",
                  "user3",
                  now,
                  null,
                  null),
              new Todo(
                  null,
                  "Call dentist",
                  "Schedule annual checkup",
                  true,
                  "user1",
                  "system",
                  now,
                  now,
                  now),
              new Todo(
                  null,
                  "Fix bug in production",
                  "High priority issue #123",
                  false,
                  "user1",
                  "user3",
                  now,
                  null,
                  null),
              new Todo(
                  null,
                  "Write documentation",
                  "Update API docs",
                  false,
                  "user2",
                  "user4",
                  now,
                  null,
                  null),
              new Todo(
                  null,
                  "Plan vacation",
                  "Research destinations",
                  true,
                  "user2",
                  "user5",
                  now,
                  now,
                  now),
              // 👇 new 5 todos
              new Todo(
                  null,
                  "Clean workspace",
                  "Organize desk and archive old files",
                  false,
                  "user2",
                  "user1",
                  now,
                  null,
                  null),
              new Todo(
                  null,
                  "Submit expense report",
                  "Expenses for August",
                  true,
                  "user3",
                  "system",
                  now,
                  now,
                  now),
              new Todo(
                  null,
                  "Prepare presentation",
                  "Slides for Monday’s meeting",
                  false,
                  "user3",
                  "user2",
                  now,
                  null,
                  null),
              new Todo(
                  null,
                  "Refactor authentication module",
                  "Improve security and code quality",
                  true,
                  "user1",
                  "user2",
                  now,
                  now,
                  now),
              new Todo(
                  null,
                  "Read new tech article",
                  "Microservices best practices",
                  false,
                  "user2",
                  "user3",
                  now,
                  null,
                  null));

      todoRepository.saveAll(todos);
      log.info("Initialized database with {} todo items", todos.size());
    };
  }
}
