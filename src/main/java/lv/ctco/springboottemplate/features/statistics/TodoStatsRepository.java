package lv.ctco.springboottemplate.features.statistics;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import lv.ctco.springboottemplate.features.statistics.dto.*;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@Repository
public class TodoStatsRepository {

  private final MongoTemplate mongoTemplate;

  public TodoStatsRepository(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  Aggregation getSummaryStatsAggregation(ResponseFormat format, Instant from, Instant to) {
    Criteria criteria = new Criteria();

    if (from != null && to != null) {
      criteria = Criteria.where("createdAt").gte(from).lte(to);
      criteria.lte(to);
    } else if (from != null) {
      criteria = Criteria.where("createdAt").gte(from);
    } else if (to != null) {
      criteria = Criteria.where("createdAt").lte(to);
    }

    MatchOperation matchStage = Aggregation.match(criteria);

    // --- First facet: counts ---
    FacetOperation countsFacet =
        Aggregation.facet(
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
                    .as("pendingTodos"))
            .as("counts")

            // --- Second facet: userStats ---
            .and(Aggregation.group("createdBy").count().as("count"))
            .as("userStats");

    if (format == ResponseFormat.DETAILED) {
      // --- Third facet: completed todos
      countsFacet =
          countsFacet
              .and(
                  Aggregation.match(Criteria.where("completed").is(true)),
                  Aggregation.project("id", "title", "createdBy", "createdAt", "updatedAt")
                      .and("updatedAt")
                      .as("completedAt"))
              .as("completedTodos")

              // --- Facet 4: pending todos ---
              .and(
                  Aggregation.match(Criteria.where("completed").is(false)),
                  Aggregation.project("id", "title", "createdBy", "createdAt"))
              .as("pendingTodos");
    }

    // --- Final project stage ---
    ProjectionOperation project =
        Aggregation.project()
            .and(ArrayOperators.ArrayElemAt.arrayOf("counts.totalTodos").elementAt(0))
            .as("totalTodos")
            .and(ArrayOperators.ArrayElemAt.arrayOf("counts.completedTodos").elementAt(0))
            .as("completedTodos")
            .and(ArrayOperators.ArrayElemAt.arrayOf("counts.pendingTodos").elementAt(0))
            .as("pendingTodos")
            .and("userStats")
            .as("userStats");

    if (format == ResponseFormat.DETAILED) {
      project =
          project
              .and(ConditionalOperators.ifNull("completedTodos").then(List.of()))
              .as("todos.completed")
              .and(ConditionalOperators.ifNull("pendingTodos").then(List.of()))
              .as("todos.pending");
    }

    // Build the full aggregation
    return Aggregation.newAggregation(matchStage, countsFacet, project);
  }

  Long extractLong(Document doc, String key) {
    Number n = doc.get(key, Number.class);
    if (n == null) {
      return 0L;
    }
    return n.longValue();
  }

  public TodoStatsDto getStats(ResponseFormat format, Instant from, Instant to) {
    Aggregation aggregation = getSummaryStatsAggregation(format, from, to);

    Document doc =
        mongoTemplate.aggregate(aggregation, "todos", Document.class).getUniqueMappedResult();

    if (doc == null) {
      return new TodoStatsDto(0, 0, 0, Map.of(), null);
    }

    List<Document> userStatsList = (List<Document>) doc.get("userStats");
    Map<String, Long> userStats =
        userStatsList.stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    d -> d.getString("_id"), d -> ((Number) d.get("count")).longValue()));

    TodoDetailsDto details = null;

    if (format == ResponseFormat.DETAILED) {
      List<CompletedTodoDto> completedTodos =
          ((List<Document>) ((Document) doc.get("todos")).get("completed"))
              .stream()
                  .map(
                      d ->
                          new CompletedTodoDto(
                              d.getString("id"),
                              d.getString("title"),
                              d.getString("createdBy"),
                              d.getDate("createdAt").toInstant(),
                              d.getDate("completedAt").toInstant()))
                  .toList();

      List<PendingTodoDto> pendingTodos =
          ((List<Document>) ((Document) doc.get("todos")).get("pending"))
              .stream()
                  .map(
                      d ->
                          new PendingTodoDto(
                              d.getString("id"),
                              d.getString("title"),
                              d.getString("createdBy"),
                              d.getDate("createdAt").toInstant()))
                  .toList();

      details = new TodoDetailsDto(completedTodos, pendingTodos);
    }

    return new TodoStatsDto(
        extractLong(doc, "totalTodos"),
        extractLong(doc, "completedTodos"),
        extractLong(doc, "pendingTodos"),
        userStats,
        details);
  }
}
