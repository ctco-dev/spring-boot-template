package lv.ctco.springboottemplate.features.statistics;

import org.springframework.lang.Nullable;

import java.util.Map;

public record TodoDetailedStatsDto(
        long totalTodos,
        long completedTodos,
        long pendingTodos,
        Map<String, Long> userStats,
        TodoDetails todos
) {}

