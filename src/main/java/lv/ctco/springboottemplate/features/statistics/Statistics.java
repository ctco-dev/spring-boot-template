package lv.ctco.springboottemplate.features.statistics;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_ABSENT)
public record Statistics(
    int totalTodos,
    int completedTodos,
    int pendingTodos,
    Map<String, Integer> userStats,
    Optional<Map<String, List<StatisticsTodo>>> todos) {}
