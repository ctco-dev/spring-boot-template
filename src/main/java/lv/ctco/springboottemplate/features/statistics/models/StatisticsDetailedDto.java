package lv.ctco.springboottemplate.features.statistics.models;

import java.util.Map;

public record StatisticsDetailedDto(
    long totalTodos,
    long completedTodos,
    long pendingTodos,
    Map<String, Long> userStats,
    StatisticsTodosDto todos) {}
