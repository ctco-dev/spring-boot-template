package lv.ctco.springboottemplate.features.statistics.dto;

public sealed interface TodoStatsDto permits TodoSummaryStatsDto, TodoDetailedStatsDto {}