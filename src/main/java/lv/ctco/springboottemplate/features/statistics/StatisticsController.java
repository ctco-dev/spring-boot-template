package lv.ctco.springboottemplate.features.statistics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lv.ctco.springboottemplate.features.statistics.dto.StatsFormatEnum;
import lv.ctco.springboottemplate.features.statistics.dto.TodoStatsResponseDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Statistics Controller", description = "Statistics endpoints")
public class StatisticsController {

  private final StatisticsService statisticsService;

  @GetMapping
  @Operation(summary = "Query aggregated statistics results")
  public TodoStatsResponseDTO query(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          Optional<LocalDate> from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          Optional<LocalDate> to,
      @RequestParam(defaultValue = "SUMMARY") StatsFormatEnum format) {
    return statisticsService.query(from, to, format);
  }
}
