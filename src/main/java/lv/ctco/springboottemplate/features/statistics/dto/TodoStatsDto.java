package lv.ctco.springboottemplate.features.statistics.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TodoStatsDto(
    long totalTodos,
    long completedTodos,
    long pendingTodos,
    Map<String, Long> userStats,
    TodoDetailsDto todos) {}
