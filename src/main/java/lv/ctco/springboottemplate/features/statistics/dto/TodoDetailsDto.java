package lv.ctco.springboottemplate.features.statistics.dto;

import java.util.List;

public record TodoDetailsDto(List<CompletedTodoDto> completed, List<PendingTodoDto> pending) {}
