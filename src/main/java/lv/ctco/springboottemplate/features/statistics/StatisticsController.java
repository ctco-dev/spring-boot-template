package lv.ctco.springboottemplate.features.statistics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import lv.ctco.springboottemplate.features.statistics.dto.TodoStatsDto;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Todo Controller", description = "Todo management endpoints")
public class StatisticsController {
  private final StatisticsService statisticsService;

  public StatisticsController(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  @GetMapping
  @Operation(summary = "Get todo statistics")
  public TodoStatsDto getStatistics(
      @Nullable @RequestParam(name = "from", required = false) LocalDate from,
      @Nullable @RequestParam(name = "to", required = false) LocalDate to,
      @RequestParam ResponseFormat format) {

    if (from == null && to == null) {
      throw new RuntimeException("Either 'from' or 'to' should be provided");
    }

    Instant fromInstant = from == null ? null : from.atStartOfDay(ZoneOffset.UTC).toInstant();
    Instant toInstant = to == null ? null : to.atStartOfDay(ZoneOffset.UTC).toInstant();

    return statisticsService.getStatistics(format, fromInstant, toInstant);
  }
}
