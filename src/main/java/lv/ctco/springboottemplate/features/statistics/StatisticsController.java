package lv.ctco.springboottemplate.features.statistics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Statistics Controller", description = "Statistics endpoint")
@Validated
public class StatisticsController {
  private final StatisticsService statisticsService;

  public StatisticsController(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  @GetMapping
  @Operation(summary = "Get todo statistics")
  public ResponseEntity<Statistics> getStatistics(
      @Parameter(description = "Start date. Format: YYYY-MM-DD", example = "2023-01-01")
          @RequestParam
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          Optional<LocalDate> from,
      @Parameter(description = "End date. Format: YYYY-MM-DD", example = "2023-12-31")
          @RequestParam
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          Optional<LocalDate> to,
      @Parameter(description = "Response format", example = "summary")
          @Schema(allowableValues = {"summary", "detailed"})
          @RequestParam
          @StatisticsFormat
          String format) {
    var statisticsFormat =
        "summary".equals(format) ? StatisticsFormats.SUMMARY : StatisticsFormats.DETAILED;
    return ResponseEntity.ok(statisticsService.getStatistics(from, to, statisticsFormat));
  }
}
