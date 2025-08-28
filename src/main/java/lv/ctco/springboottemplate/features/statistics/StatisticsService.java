package lv.ctco.springboottemplate.features.statistics;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {
  private final MongoTemplate mongoTemplate;

  public StatisticsService(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  public Statistics getStatisticsSummary(
      Optional<LocalDate> from, Optional<LocalDate> to, String format) {
    Document result = getTodosStatisticsAggregation(from, to, format).getUniqueMappedResult();

    int totalTodos = getTodoCount(result, "totalTodos");
    int completedTodos = getTodoCount(result, "completedTodos");
    int pendingTodos = getTodoCount(result, "pendingTodos");

    Map<String, Integer> userStats = getUserStats(result);

    if (format.equals("summary")) {
      return new Statistics(totalTodos, completedTodos, pendingTodos, userStats, Optional.empty());
    }

    Map<String, List<StatisticsTodo>> todos = getStatisticsTodos(result);

    return new Statistics(totalTodos, completedTodos, pendingTodos, userStats, Optional.of(todos));
  }

  private AggregationResults<Document> getTodosStatisticsAggregation(
      Optional<LocalDate> from, Optional<LocalDate> to, String format) {
    MatchOperation matchTodosInRange = matchDateRange(from, to);
    MatchOperation matchCompletedTodos = match(where("completed").is(true));
    MatchOperation matchPendingTodos = match(where("completed").is(false));

    GroupOperation countTodos = group().count().as("count");
    GroupOperation countTodosPerUser = group("createdBy").count().as("count");

    ProjectionOperation projectPendingFields = project("id", "title", "createdBy", "createdAt");
    ProjectionOperation projectCompletedFields =
        projectPendingFields.andExpression("updatedAt").as("completedAt");

    FacetOperation summaryFacet =
        facet(countTodos)
            .as("totalTodos")
            .and(matchCompletedTodos, countTodos)
            .as("completedTodos")
            .and(matchPendingTodos, countTodos)
            .as("pendingTodos")
            .and(countTodosPerUser)
            .as("userStats");

    FacetOperation detailsFacet =
        facet(countTodos)
            .as("totalTodos")
            .and(matchCompletedTodos, countTodos)
            .as("completedTodos")
            .and(matchPendingTodos, countTodos)
            .as("pendingTodos")
            .and(countTodosPerUser)
            .as("userStats")
            .and(matchPendingTodos, projectPendingFields)
            .as("pending")
            .and(matchCompletedTodos, projectCompletedFields)
            .as("completed");

    Aggregation summaryAggregation = newAggregation(matchTodosInRange, summaryFacet);

    Aggregation detailedAggregation = newAggregation(matchTodosInRange, detailsFacet);

    if (Objects.equals(format, "summary")) {
      return this.mongoTemplate.aggregate(summaryAggregation, "todos", Document.class);
    } else {
      return this.mongoTemplate.aggregate(detailedAggregation, "todos", Document.class);
    }
  }

  private MatchOperation matchDateRange(Optional<LocalDate> from, Optional<LocalDate> to) {
    Criteria criteria =
        Stream.of(
                from.map(f -> where("createdAt").gte(f.atStartOfDay())),
                to.map(t -> where("createdAt").lte(t.atTime(LocalTime.MAX))))
            .flatMap(Optional::stream)
            .reduce(
                new Criteria(),
                (c1, c2) ->
                    c1.getCriteriaObject().isEmpty() ? c2 : new Criteria().andOperator(c1, c2));
    return Aggregation.match(criteria);
  }

  private int getTodoCount(Document result, String key) {
    List<Document> totalTodosDocument = result.getList(key, Document.class);
    if (totalTodosDocument.isEmpty()) {
      return 0;
    }
    return (int) totalTodosDocument.getFirst().get("count");
  }

  private Map<String, Integer> getUserStats(Document result) {
    List<Document> userStatsDocument = result.getList("userStats", Document.class);
    return userStatsDocument.stream()
        .collect(
            Collectors.toMap(
                stat -> (String) stat.get("_id"), stat -> (Integer) stat.get("count")));
  }

  private Map<String, List<StatisticsTodo>> getStatisticsTodos(Document result) {
    List<Document> pendingDocument = result.getList("pending", Document.class);
    List<StatisticsTodo> pending =
        pendingDocument.stream()
            .map(
                item ->
                    new StatisticsTodo(
                        item.get("_id").toString(),
                        item.get("title").toString(),
                        item.get("createdBy").toString(),
                        ((Date) item.get("createdAt")).toInstant(),
                        Optional.empty()))
            .toList();

    List<Document> completedDocument = result.getList("completed", Document.class);
    List<StatisticsTodo> completed =
        completedDocument.stream()
            .map(
                item ->
                    new StatisticsTodo(
                        item.get("_id").toString(),
                        item.get("title").toString(),
                        item.get("createdBy").toString(),
                        ((Date) item.get("createdAt")).toInstant(),
                        Optional.ofNullable(((Date) item.get("completedAt")).toInstant())))
            .toList();
    Map<String, List<StatisticsTodo>> todos = new HashMap<>();
    todos.put("completed", completed);
    todos.put("pending", pending);
    return todos;
  }
}
