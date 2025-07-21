package lv.ctco.springboottemplate.features.statistics;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Service
public class StatisticsAggregationService {
  private final MongoTemplate mongoTemplate;

  public StatisticsAggregationService(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  public StatisticsAggregationResult getAllStatistics(
      Optional<LocalDate> from, Optional<LocalDate> to) {
    MatchOperation matchOperation = buildDateMatchOperation(from, to);

    GroupOperation basicStatsAggregation =
        Aggregation.group()
            .count()
            .as("totalTodos")
            .sum(
                ConditionalOperators.when(Criteria.where("completed").is(true))
                    .then(1)
                    .otherwise(0))
            .as("completedTodos")
            .sum(
                ConditionalOperators.when(Criteria.where("completed").is(false))
                    .then(1)
                    .otherwise(0))
            .as("pendingTodos");

    GroupOperation userStatsAggregation = Aggregation.group("createdBy").count().as("count");

    MatchOperation completedTodosAggregation =
        Aggregation.match(Criteria.where("completed").is(true));
    ProjectionOperation completedTodosProjection =
        Aggregation.project()
            .and("id")
            .as("id")
            .and("title")
            .as("title")
            .and("createdBy")
            .as("createdBy")
            .and("createdAt")
            .as("createdAt")
            .and("updatedAt")
            .as("completedAt");

    MatchOperation pendingTodosAggregation =
        Aggregation.match(Criteria.where("completed").is(false));
    ProjectionOperation pendingTodosProjection =
        Aggregation.project()
            .and("id")
            .as("id")
            .and("title")
            .as("title")
            .and("createdBy")
            .as("createdBy")
            .and("createdAt")
            .as("createdAt")
            .andExpression("null")
            .as("completedAt");

    FacetOperation facetOperation =
        Aggregation.facet()
            .and(basicStatsAggregation)
            .as("basicStats")
            .and(userStatsAggregation)
            .as("userStats")
            .and(completedTodosAggregation, completedTodosProjection)
            .as("completedTodos")
            .and(pendingTodosAggregation, pendingTodosProjection)
            .as("pendingTodos");

    Aggregation aggregation = Aggregation.newAggregation(matchOperation, facetOperation);

    AggregationResults<Document> results =
        mongoTemplate.aggregate(aggregation, "todos", Document.class);

    Document result = results.getUniqueMappedResult();

    if (result == null || result.isEmpty()) {
      return new StatisticsAggregationResult(
          new BasicStatisticsResult(0, 0, 0), List.of(), List.of(), List.of());
    }

    List<Document> basicStatsList = result.getList("basicStats", Document.class);
    BasicStatisticsResult basicStats =
        basicStatsList.isEmpty()
            ? new BasicStatisticsResult(0, 0, 0)
            : new BasicStatisticsResult(
                basicStatsList.get(0).getInteger("totalTodos"),
                basicStatsList.get(0).getInteger("completedTodos"),
                basicStatsList.get(0).getInteger("pendingTodos"));

    List<Document> userStatsList = result.getList("userStats", Document.class);
    List<UserStatsResult> userStats =
        userStatsList.stream()
            .map(doc -> new UserStatsResult(doc.getString("_id"), doc.getInteger("count"), 0, 0))
            .toList();

    List<Document> completedTodosList = result.getList("completedTodos", Document.class);
    List<CompactTodo> completedTodos =
        completedTodosList.stream()
            .map(
                doc ->
                    new CompactTodo(
                        doc.getString("id"),
                        doc.getString("title"),
                        doc.getString("createdBy"),
                        doc.getDate("createdAt").toInstant(),
                        Optional.of(doc.getDate("completedAt").toInstant())))
            .toList();

    List<Document> pendingTodosList = result.getList("pendingTodos", Document.class);
    List<CompactTodo> pendingTodos =
        pendingTodosList.stream()
            .map(
                doc ->
                    new CompactTodo(
                        doc.getString("id"),
                        doc.getString("title"),
                        doc.getString("createdBy"),
                        doc.getDate("createdAt").toInstant(),
                        Optional.empty()))
            .toList();

    return new StatisticsAggregationResult(basicStats, userStats, completedTodos, pendingTodos);
  }

  private MatchOperation buildDateMatchOperation(Optional<LocalDate> from, Optional<LocalDate> to) {
    if (from.isEmpty() && to.isEmpty()) {
      return Aggregation.match(new Criteria());
    }

    if (from.isPresent() && to.isPresent()) {
      LocalDateTime fromDateTime = from.get().atStartOfDay();
      LocalDateTime toDateTime = to.get().atTime(LocalTime.MAX);

      Criteria dateCriteria =
          new Criteria()
              .andOperator(
                  Criteria.where("createdAt").gte(fromDateTime),
                  Criteria.where("createdAt").lte(toDateTime));
      return Aggregation.match(dateCriteria);
    } else if (from.isPresent()) {
      LocalDateTime fromDateTime = from.get().atStartOfDay();
      return Aggregation.match(Criteria.where("createdAt").gte(fromDateTime));
    } else {
      LocalDateTime toDateTime = to.get().atTime(LocalTime.MAX);
      return Aggregation.match(Criteria.where("createdAt").lte(toDateTime));
    }
  }
}
