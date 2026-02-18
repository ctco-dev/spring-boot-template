package lv.ctco.springboottemplate.features.statistics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsDetailedDto;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsRequestDto;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsSummaryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  public ResponseEntity<?> getStatistics(@Valid @ModelAttribute StatisticsRequestDto input) {
    LocalDate fromDate = input.fromDate();
    LocalDate toDate = input.toDate();

    if ("summary".equalsIgnoreCase(input.normalizedFormat())) {
      StatisticsSummaryDto summary = statisticsService.getSummary(fromDate, toDate);
      return ResponseEntity.ok(summary);
    }

    StatisticsDetailedDto detailed = statisticsService.getDetailed(fromDate, toDate);
    return ResponseEntity.ok(detailed);
  }
}
