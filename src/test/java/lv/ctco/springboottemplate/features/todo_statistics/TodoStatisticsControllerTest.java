package lv.ctco.springboottemplate.features.todo_statistics;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import lv.ctco.springboottemplate.features.todo.Todo;
import lv.ctco.springboottemplate.features.todo.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class TodoStatisticsControllerIT {

  @Container static final MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

  @DynamicPropertySource
  static void configure(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private TodoRepository todoRepository;

  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    todoRepository.deleteAll();

    Instant now = Instant.now();

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
                null));

    todoRepository.saveAll(todos);
  }

  @Test
  void shouldReturnStatistics() throws Exception {
    mockMvc
        .perform(
            get("/api/statistics")
                .param("from", Instant.now().minusSeconds(3600).toString())
                .param("to", Instant.now().plusSeconds(3600).toString())
                .param("format", "detailed")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalTodos").value(3))
        .andExpect(jsonPath("$.completedTodos").value(1))
        .andExpect(jsonPath("$.pendingTodos").value(2))
        .andExpect(jsonPath("$.userStats.user1").value(2))
        .andExpect(jsonPath("$.todos.completed").isArray());
  }
}
