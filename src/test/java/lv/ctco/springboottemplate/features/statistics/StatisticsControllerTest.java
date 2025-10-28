package lv.ctco.springboottemplate.features.statistics;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import lv.ctco.springboottemplate.config.ResponseFormatConverter;
import lv.ctco.springboottemplate.features.statistics.dto.TodoStatsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(ResponseFormatConverter.class)
class StatisticsControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());

  @Test
  void getStatisticsFailsIncorrectFormat() throws Exception {
    mockMvc
        .perform(get("/api/statistics").param("format", "unknown"))
        .andExpect(status().isBadRequest())
        .andExpect(
            content().string(containsString("Method parameter 'format': Failed to convert")));
  }

  @ParameterizedTest
  @ValueSource(strings = {"summary", "detailed"})
  void getStatisticsFailsMissingFromAndToWithCorrectFormat(String format) throws Exception {
    mockMvc
        .perform(get("/api/statistics").param("format", format))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Either 'from' or 'to' should be provided")));
  }

  @Test
  void getSummaryStatisticsSucceeds() throws Exception {
    var now = Instant.now().minus(1L, ChronoUnit.DAYS);

    MvcResult result =
        mockMvc
            .perform(
                get("/api/statistics")
                    .param("format", "summary")
                    .param("from", formatter.format(now)))
            .andExpect(status().isOk())
            .andReturn();

    String json = result.getResponse().getContentAsString();
    TodoStatsDto statistics = objectMapper.readValue(json, TodoStatsDto.class);

    TodoStatsDto expected =
        new TodoStatsDto(
            7,
            3,
            4,
            Map.of(
                "system", 5L,
                "warlock", 2L),
            null);

    assertEquals(expected, statistics);
  }

  @Test
  void getDetailedStatisticsSucceeds() throws Exception {
    var now = Instant.now().minus(1L, ChronoUnit.DAYS);

    MvcResult result =
        mockMvc
            .perform(
                get("/api/statistics")
                    .param("format", "detailed")
                    .param("from", formatter.format(now)))
            .andExpect(status().isOk())
            .andReturn();

    String json = result.getResponse().getContentAsString();
    TodoStatsDto statistics = objectMapper.readValue(json, TodoStatsDto.class);

    assertEquals(3, statistics.todos().completed().size());
    assertEquals(4, statistics.todos().pending().size());
  }

  @Test
  void getDetailedStatisticsToDateSucceeds() throws Exception {
    // get todo stats before now
    var now = Instant.now().minus(1L, ChronoUnit.DAYS);

    MvcResult result =
        mockMvc
            .perform(
                get("/api/statistics")
                    .param("format", "detailed")
                    .param("to", formatter.format(now)))
            .andExpect(status().isOk())
            .andReturn();

    String json = result.getResponse().getContentAsString();
    TodoStatsDto statistics = objectMapper.readValue(json, TodoStatsDto.class);

    assertEquals(0, statistics.pendingTodos());
    assertEquals(1, statistics.completedTodos());
    assertEquals(1, statistics.todos().completed().size());
    assertEquals(0, statistics.todos().pending().size());
  }
}
