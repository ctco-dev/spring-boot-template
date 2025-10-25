package lv.ctco.springboottemplate.features.statistics.dto;

import java.util.Map;

public record TodoDetailedStatsDto(
        long totalTodos,
        long completedTodos,
        long pendingTodos,
        Map<String, Long> userStats,
        TodoDetailsDto todos
) implements TodoStatsDto {}

