package lv.ctco.springboottemplate.features.statistics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsErrorResponse;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsRequest;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsResponse;
import lv.ctco.springboottemplate.features.statistics.services.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Todo Statistics Controller", description = "Todo statistics related endpoints")
public class StatisticsController {
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping
    @Operation(
            summary = "Get todo statistics",
            description = "Returns summary or detailed todo statistics. Optional date filters are applied to createdAt field.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Statistics computed successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content(schema = @Schema(implementation = StatisticsErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = StatisticsErrorResponse.class)))
            }
    )
    public ResponseEntity<?> getStatistics(StatisticsRequest request) {
        StatisticsResponse response = statisticsService.computeStatistics(request);
        return ResponseEntity.ok(response);
    }
}
