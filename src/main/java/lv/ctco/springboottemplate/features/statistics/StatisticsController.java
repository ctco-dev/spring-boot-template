package lv.ctco.springboottemplate.features.statistics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import lv.ctco.springboottemplate.features.statistics.dto.ResponseFormat;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<Object> getStatistics(
      @Nullable @RequestParam(name = "from", required = false) LocalDate from,
      @Nullable @RequestParam(name = "to", required = false) LocalDate to,
      @RequestParam(name = "format") String formatString) {

    ResponseFormat format = ResponseFormat.SUMMARY;
    try {
      format = ResponseFormat.valueOf(formatString.toUpperCase());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body("Method parameter 'format': Failed to convert");
    }

    if (from == null && to == null) {
      return ResponseEntity.badRequest().body("Either 'from' or 'to' should be provided");
    }

    Instant fromInstant = from == null ? null : from.atStartOfDay(ZoneOffset.UTC).toInstant();
    Instant toInstant = to == null ? null : to.atStartOfDay(ZoneOffset.UTC).toInstant();

    return ResponseEntity.ok(statisticsService.getStatistics(format, fromInstant, toInstant));
  }
}
