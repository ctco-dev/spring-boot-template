package lv.ctco.springboottemplate.features.statistics;

import java.util.Map;

public record TodoSummaryStatsDto(
    long totalTodos,
    long completedTodos,
    long pendingTodos,
    Map<String, Long> userStats
){}
