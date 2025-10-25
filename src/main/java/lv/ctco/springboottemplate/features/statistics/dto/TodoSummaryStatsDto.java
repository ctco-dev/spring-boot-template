package lv.ctco.springboottemplate.features.statistics.dto;

import java.util.Map;

public record TodoSummaryStatsDto(
    long totalTodos,
    long completedTodos,
    long pendingTodos,
    Map<String, Long> userStats
) implements TodoStatsDto {}
