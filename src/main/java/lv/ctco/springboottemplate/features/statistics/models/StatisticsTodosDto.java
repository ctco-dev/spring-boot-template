package lv.ctco.springboottemplate.features.statistics.models;

import java.util.List;

public record StatisticsTodosDto(
    List<StatisticsTodoItemDto> completed, List<StatisticsTodoItemDto> pending) {}
