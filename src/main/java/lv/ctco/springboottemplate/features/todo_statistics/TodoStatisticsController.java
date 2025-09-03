package lv.ctco.springboottemplate.features.todo_statistics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lv.ctco.springboottemplate.features.todo.Todo;
import lv.ctco.springboottemplate.features.todo_statistics.dto.TodoStatistics;
import lv.ctco.springboottemplate.features.todo_statistics.dto.TodoStatisticsRequest;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** REST controller for getting statistics for {@link Todo} items. */
@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Statistics Controller", description = "Provides statistics for Todo items")
public class TodoStatisticsController {
  private final TodoStatisticsService statisticsService;

  public TodoStatisticsController(TodoStatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  @GetMapping
  @Operation(summary = "Get todo items statistics")
  public ResponseEntity<TodoStatistics> getStatistics(
      @Valid @ParameterObject @ModelAttribute TodoStatisticsRequest request) {

    TodoStatistics stats = statisticsService.makeStatistics(request);

    return ResponseEntity.ok(stats);
  }
}
