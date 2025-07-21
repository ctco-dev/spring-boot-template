package lv.ctco.springboottemplate.features.statistics;

import java.util.List;

public record TodoStatistics(List<CompactTodo> completed, List<CompactTodo> pending) {}
