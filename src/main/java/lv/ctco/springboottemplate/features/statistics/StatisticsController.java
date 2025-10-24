package lv.ctco.springboottemplate.features.statistics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
    public TodoSummaryStatsDto getStatistics(
            @Nullable @RequestParam(name = "from", required = false) LocalDate from,
            @Nullable @RequestParam(name = "to", required = false) LocalDate to,
            @RequestParam ResponseFormat format) {

        if (from == null && to == null) {
            throw new RuntimeException("Either 'from' or 'to' should be provided");
        }

        return statisticsService.getStatistics();
    }

    @GetMapping("/expanded")
    @Operation(summary = "Get todo statistics")
    public TodoSummaryStatsDto getExpandedStatistics(
            @Nullable @RequestParam(name = "from", required = false) LocalDate from,
            @Nullable @RequestParam(name = "to", required = false) LocalDate to,
            @RequestParam ResponseFormat format) {

        if (from == null && to == null) {
            throw new RuntimeException("Either 'from' or 'to' should be provided");
        }

        return statisticsService.getExpandedStatistics();
    }
}
