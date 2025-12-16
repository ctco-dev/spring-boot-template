package lv.ctco.springboottemplate.features.statistics.models;

import java.util.List;

public record TodosSection(List<TodosStatistics> completed, List<TodosStatistics> pending) {
}
