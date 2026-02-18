package lv.ctco.springboottemplate.features.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsDetailedDto;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsSummaryDto;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsTodoItemDto;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsTodosDto;
import lv.ctco.springboottemplate.features.todo.Todo;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

@Service
public class StatisticsMapperService {

  public StatisticsSummaryDto toSummary(AggregationResults<Document> results) {
    long total = 0L;
    long completed = 0L;
    Map<String, Long> userStats = new HashMap<>();

    for (Document doc : results) {
      AggregatedRow row = toAggregatedRow(doc);

      total += row.count();
      if (row.completed()) {
        completed += row.count();
      }
      userStats.merge(row.createdBy(), row.count(), Long::sum);
    }

    long pending = total - completed;
    return new StatisticsSummaryDto(total, completed, pending, userStats);
  }

  public StatisticsDetailedDto toDetailed(
      StatisticsSummaryDto summary, StatisticsTodosDto todosDto) {
    return new StatisticsDetailedDto(
        summary.totalTodos(),
        summary.completedTodos(),
        summary.pendingTodos(),
        summary.userStats(),
        todosDto);
  }

  public StatisticsTodosDto toTodosDto(List<Todo> todos) {
    List<StatisticsTodoItemDto> completedTodos =
        todos.stream()
            .filter(Todo::completed)
            .map(
                todo ->
                    new StatisticsTodoItemDto(
                        todo.id(),
                        todo.title(),
                        todo.createdBy(),
                        todo.createdAt(),
                        todo.updatedAt()))
            .toList();

    List<StatisticsTodoItemDto> pendingTodos =
        todos.stream()
            .filter(todo -> !todo.completed())
            .map(
                todo ->
                    new StatisticsTodoItemDto(
                        todo.id(), todo.title(), todo.createdBy(), todo.createdAt(), null))
            .toList();

    return new StatisticsTodosDto(completedTodos, pendingTodos);
  }

  private AggregatedRow toAggregatedRow(Document doc) {
    Document id = (Document) doc.get("_id");
    String createdBy = id.getString("createdBy");
    boolean isCompleted = Boolean.TRUE.equals(id.getBoolean("completed"));
    Number countNumber = doc.get("count", Number.class);
    long count = countNumber != null ? countNumber.longValue() : 0L;

    return new AggregatedRow(createdBy, isCompleted, count);
  }

  private record AggregatedRow(String createdBy, boolean completed, long count) {}
}
