package lv.ctco.springboottemplate.features.statistics;

import java.util.List;

public record StatisticsAggregationResult(
    BasicStatisticsResult basicStats,
    List<UserStatsResult> userStats,
    List<CompactTodo> completedTodos,
    List<CompactTodo> pendingTodos) {}
