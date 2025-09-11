package lv.ctco.springboottemplate.features.statistics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Statistics Controller", description = "Statistics endpoints")
public class StatisticsController {
  private final StatisticsService statisticsService;

  public StatisticsController(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  @GetMapping
  @Operation(summary = "Get Insights About Todo Items")
  public Boolean getInsightsAboutTodoItems(
    @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @io.swagger.v3.oas.annotations.Parameter(
      description = "2025-01-01",
      schema = @io.swagger.v3.oas.annotations.media.Schema(type = "string", pattern = "\\d{4}-\\d{2}-\\d{2}", example = "2025-01-01")
    ) LocalDate from,
    @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @io.swagger.v3.oas.annotations.Parameter(
      description = "2025-01-31",
      schema = @io.swagger.v3.oas.annotations.media.Schema(type = "string", pattern = "\\d{4}-\\d{2}-\\d{2}", example = "2025-01-31")
    ) LocalDate to,
    @RequestParam(required = true) StatisticsFormat format
  ) {
    if (from.isAfter(to)) {
      throw new org.springframework.web.server.ResponseStatusException(
        org.springframework.http.HttpStatus.BAD_REQUEST,
        "Parameter 'from' must be before or equal to 'to'"
      );
    }

    if (!java.util.EnumSet.allOf(StatisticsFormat.class).contains(format)) {
      throw new org.springframework.web.server.ResponseStatusException(
        org.springframework.http.HttpStatus.BAD_REQUEST,
        "Parameter 'format' must be one of " + java.util.Arrays.toString(StatisticsFormat.values())
      );
    }

    return true;
  }
}
