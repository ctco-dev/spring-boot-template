package lv.ctco.springboottemplate.features.statistics;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsDetailedDto;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsSummaryDto;
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
  private final StatisticsMapper statisticsMapper;

  public StatisticsService(
      TodoService todoService, MongoTemplate mongoTemplate, StatisticsMapper statisticsMapper) {
    this.todoService = todoService;
    this.mongoTemplate = mongoTemplate;
    this.statisticsMapper = statisticsMapper;
  }

  public StatisticsSummaryDto getSummary(LocalDate from, LocalDate to) {
    return aggregateSummary(from, to);
  }

  public StatisticsDetailedDto getDetailed(LocalDate from, LocalDate to) {
    StatisticsSummaryDto summary = aggregateSummary(from, to);
    List<Todo> todos = findTodosInRange(from, to);
    StatisticsTodosDto todosDto = statisticsMapper.toTodosDto(todos);
    return statisticsMapper.toDetailed(summary, todosDto);
  }

  private StatisticsSummaryDto aggregateSummary(LocalDate from, LocalDate to) {
    Aggregation aggregation = buildAggregation(from, to);
    AggregationResults<Document> results =
        mongoTemplate.aggregate(aggregation, "todos", Document.class);
    return statisticsMapper.toSummary(results);
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
}
