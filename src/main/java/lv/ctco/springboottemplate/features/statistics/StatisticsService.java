package lv.ctco.springboottemplate.features.statistics;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsDetailedDto;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsSummaryDto;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsTodoItemDto;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsTodosDto;
import lv.ctco.springboottemplate.features.todo.Todo;
import lv.ctco.springboottemplate.features.todo.TodoService;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

  private final TodoService todoService;
  private final MongoTemplate mongoTemplate;

  public StatisticsService(TodoService todoService, MongoTemplate mongoTemplate) {
    this.todoService = todoService;
    this.mongoTemplate = mongoTemplate;
  }

  public StatisticsSummaryDto getSummary(LocalDate from, LocalDate to) {
    return aggregateSummary(from, to);
  }

  public StatisticsDetailedDto getDetailed(LocalDate from, LocalDate to) {
    StatisticsSummaryDto summary = aggregateSummary(from, to);
    List<Todo> todos = findTodosInRange(from, to);
    StatisticsTodosDto todosDto = buildTodosDto(todos);

    return new StatisticsDetailedDto(
        summary.totalTodos(),
        summary.completedTodos(),
        summary.pendingTodos(),
        summary.userStats(),
        todosDto);
  }

  private StatisticsSummaryDto aggregateSummary(LocalDate from, LocalDate to) {
    Aggregation aggregation = buildAggregation(from, to);
    AggregationResults<Document> results =
        mongoTemplate.aggregate(aggregation, "todos", Document.class);
    return mapResultsToSummary(results);
  }

  private Aggregation buildAggregation(LocalDate from, LocalDate to) {
    Criteria dateCriteria = buildDateCriteria(from, to);

    GroupOperation groupByUserAndCompletion =
        Aggregation.group("createdBy", "completed").count().as("count");

    if (dateCriteria != null) {
      MatchOperation match = Aggregation.match(dateCriteria);
      return Aggregation.newAggregation(match, groupByUserAndCompletion);
    }

    return Aggregation.newAggregation(groupByUserAndCompletion);
  }

  private StatisticsSummaryDto mapResultsToSummary(AggregationResults<Document> results) {
    long total = 0L;
    long completed = 0L;
    Map<String, Long> userStats = new HashMap<>();

    for (Document doc : results) {
      Document id = (Document) doc.get("_id");
      String createdBy = id.getString("createdBy");
      boolean isCompleted = Boolean.TRUE.equals(id.getBoolean("completed"));
      Number countNumber = doc.get("count", Number.class);
      long count = countNumber != null ? countNumber.longValue() : 0L;

      total += count;
      if (isCompleted) {
        completed += count;
      }
      userStats.merge(createdBy, count, Long::sum);
    }

    long pending = total - completed;
    return new StatisticsSummaryDto(total, completed, pending, userStats);
  }

  private List<Todo> findTodosInRange(LocalDate from, LocalDate to) {
    Instant fromInstant = from != null ? atStartOfDay(from) : null;
    Instant toInstant = to != null ? atEndOfDay(to) : null;

    return todoService.getTodosByCreatedAtRange(fromInstant, toInstant);
  }

  private Criteria buildDateCriteria(LocalDate from, LocalDate to) {
    Instant fromInstant = from != null ? atStartOfDay(from) : null;
    Instant toInstant = to != null ? atEndOfDay(to) : null;

    if (fromInstant != null && toInstant != null) {
      return Criteria.where("createdAt").gte(fromInstant).lte(toInstant);
    } else if (fromInstant != null) {
      return Criteria.where("createdAt").gte(fromInstant);
    } else if (toInstant != null) {
      return Criteria.where("createdAt").lte(toInstant);
    }

    return null;
  }

  private Instant atStartOfDay(LocalDate date) {
    return date.atStartOfDay().toInstant(ZoneOffset.UTC);
  }

  private Instant atEndOfDay(LocalDate date) {
    return date.atTime(23, 59, 59).toInstant(ZoneOffset.UTC);
  }

  private StatisticsTodosDto buildTodosDto(List<Todo> todos) {
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
}
