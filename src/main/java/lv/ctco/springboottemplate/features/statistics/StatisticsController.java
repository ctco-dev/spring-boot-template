package lv.ctco.springboottemplate.features.statistics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsDetailedDto;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsSummaryDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Statistics Controller", description = "Todo statistics endpoints")
public class StatisticsController {

  private final StatisticsService statisticsService;

  public StatisticsController(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  @GetMapping
  @Operation(summary = "Get todo statistics")
  public ResponseEntity<?> getStatistics(
      @RequestParam(required = false) String from,
      @RequestParam(required = false) String to,
      @RequestParam(defaultValue = "summary") String format) {

    LocalDate fromDate = parseDateOrNull(from, "from");
    LocalDate toDate = parseDateOrNull(to, "to");

    if (!format.equalsIgnoreCase("summary") && !format.equalsIgnoreCase("detailed")) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "format must be either 'summary' or 'detailed'");
    }

    if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "'from' date must be before or equal to 'to' date");
    }

    if (format.equalsIgnoreCase("summary")) {
      StatisticsSummaryDto summary = statisticsService.getSummary(fromDate, toDate);
      return ResponseEntity.ok(summary);
    }

    StatisticsDetailedDto detailed = statisticsService.getDetailed(fromDate, toDate);
    return ResponseEntity.ok(detailed);
  }

  private LocalDate parseDateOrNull(String raw, String paramName) {
    if (raw == null || raw.isBlank()) {
      return null;
    }

    try {
      return LocalDate.parse(raw);
    } catch (DateTimeParseException ex) {
      String message =
          String.format("Invalid '%s' date. Expected format is yyyy-MM-dd.", paramName);

      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message, ex);
    }
  }
}
