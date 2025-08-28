package lv.ctco.springboottemplate.features.statistics;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record Statistics(
    int totalTodos,
    int completedTodos,
    int pendingTodos,
    Map<String, Integer> userStats,
    Optional<Map<String, List<StatisticsTodo>>> todos) {}
