package lv.ctco.springboottemplate.features.statistics.dto;

import java.util.List;

public record TodoBreakdownDTO(List<TodoItemDTO> completed, List<TodoItemDTO> pending) {}
