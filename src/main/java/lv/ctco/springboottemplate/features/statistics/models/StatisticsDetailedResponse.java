package lv.ctco.springboottemplate.features.statistics.models;

import java.util.Map;

public record StatisticsDetailedResponse(
        int totalTodos,
        int completedTodos,
        int pendingTodos,
        Map<String, Integer> userStats,
        TodosSection todos
) implements StatisticsResponse {
}
