package lv.ctco.springboottemplate.features.statistics.models;

import java.util.Map;

public record StatisticsSummaryDto(
    long totalTodos, long completedTodos, long pendingTodos, Map<String, Long> userStats) {}
