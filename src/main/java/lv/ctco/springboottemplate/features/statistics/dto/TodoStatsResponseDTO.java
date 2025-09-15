package lv.ctco.springboottemplate.features.statistics.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TodoStatsResponseDTO(
    int totalTodos,
    int completedTodos,
    int pendingTodos,
    Map<String, Integer> userStats,
    Optional<TodoBreakdownDTO> todos) {}
