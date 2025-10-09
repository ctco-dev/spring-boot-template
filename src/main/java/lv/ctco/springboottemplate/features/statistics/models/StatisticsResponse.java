package lv.ctco.springboottemplate.features.statistics.models;

import java.util.Map;

public interface StatisticsResponse {
    int totalTodos();

    int completedTodos();

    int pendingTodos();

    Map<String, Integer> userStats();
}
