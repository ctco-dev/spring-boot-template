package lv.ctco.springboottemplate.features.statistics.models;

import io.swagger.v3.oas.annotations.media.Schema;

public record StatisticsRequest(
        @Schema(description = "Start date (inclusive), format: yyyy-MM-dd", example = "2023-01-01")
        String from,
        @Schema(description = "End date (inclusive), format: yyyy-MM-dd", example = "2023-12-31")
        String to,
        @Schema(description = "Response format: summary | detailed (default: summary)", example = "summary")
        String format
) {
}