package lv.ctco.springboottemplate.features.statistics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Statistics Controller", description = "Statistics endpoints")
public class StatisticsController {

  private final StatisticsService statisticsService;

  public StatisticsController(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  @GetMapping
  @Operation(
      summary = "Get todo statistics",
      description =
          "Retrieve statistics about todos with optional date filtering and format options")
  public ResponseEntity<Statistics> getStatistics(
      @Parameter(description = "Start date (YYYY-MM-DD)")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate from,
      @Parameter(description = "End date (YYYY-MM-DD)")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate to,
      @Parameter(description = "Response format: summary or detailed")
          @RequestParam(defaultValue = "summary")
          String format) {
    try {
      StatisticsFormat statisticsFormat = StatisticsFormat.valueOf(format);

      GetStatisticsParams params =
          new GetStatisticsParams(
              Optional.ofNullable(from), Optional.ofNullable(to), statisticsFormat);

      Statistics statistics = statisticsService.getStatistics(params);
      return ResponseEntity.ok(statistics);

    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
