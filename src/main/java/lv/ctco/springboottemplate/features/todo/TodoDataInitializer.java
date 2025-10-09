package lv.ctco.springboottemplate.features.todo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

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
                          "Test Todo",
                          "Research destinations",
                          true,
                          "user1",
                          "user1",
                          LocalDate.of(2019, 1, 1).atStartOfDay().toInstant(java.time.ZoneOffset.UTC),
                          LocalDate.of(2023, 1, 1).atStartOfDay().toInstant(java.time.ZoneOffset.UTC)),
                  new Todo(
                          null,
                          "Test Todo2",
                          "Research destinations",
                          false,
                          "user2",
                          "user2",
                          LocalDate.of(2023, 1, 1).atStartOfDay().toInstant(java.time.ZoneOffset.UTC),
                          LocalDate.of(2024, 1, 1).atStartOfDay().toInstant(java.time.ZoneOffset.UTC))
          );

      todoRepository.saveAll(todos);
      log.info("Initialized database with {} todo items", todos.size());
    };
  }
}
