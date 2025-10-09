package lv.ctco.springboottemplate.features.statistics.models;

import java.util.Map;

public record StatisticsSummaryResponse(
        int totalTodos,
        int completedTodos,
        int pendingTodos,
        Map<String, Integer> userStats
) implements StatisticsResponse {
}
