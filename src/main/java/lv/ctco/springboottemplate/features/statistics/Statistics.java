package lv.ctco.springboottemplate.features.statistics;

import java.util.Map;
import java.util.Optional;

public record Statistics(
    long totalTodos,
    long completedTodos,
    long pendingTodos,
    Map<String, Long> userStats,
    Optional<TodoStatistics> todos) {}
