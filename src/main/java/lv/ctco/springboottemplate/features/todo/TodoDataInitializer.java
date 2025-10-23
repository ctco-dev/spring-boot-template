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
                  null, "Buy groceries", "Milk, eggs, bread", false, "system", "system", now, now),
              new Todo(
                  null,
                  "Call dentist",
                  "Schedule annual checkup",
                  true,
                  "system",
                  "system",
                  now,
                  now),
              new Todo(
                  null,
                  "Fix bug in production",
                  "High priority issue #123",
                  false,
                  "system",
                  "system",
                  now,
                  now),
              new Todo(
                  null,
                  "Write documentation",
                  "Update API docs",
                  false,
                  "system",
                  "system",
                  now,
                  now),
              new Todo(
                  null,
                  "Plan vacation",
                  "Research destinations",
                  true,
                  "system",
                  "system",
                  now,
                  now),
              new Todo(
                      null,
                      "Plan Task 2",
                      "Research mongo aggregates",
                      true,
                      "warlock",
                      "warlock",
                      now,
                      now),
                  new Todo(
                          null,
                          "Task 2 detailed dto",
                          "Research mongo aggregates",
                          false,
                          "warlock",
                          "warlock",
                          now,
                          now)
                  );

      todoRepository.saveAll(todos);
      log.info("Initialized database with {} todo items", todos.size());
    };
  }
}
