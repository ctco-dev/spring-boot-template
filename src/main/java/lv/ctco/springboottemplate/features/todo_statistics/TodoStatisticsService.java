package lv.ctco.springboottemplate.features.todo_statistics;

import com.mongodb.BasicDBObject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import lv.ctco.springboottemplate.features.todo_statistics.dto.TodoStatistics;
import lv.ctco.springboottemplate.features.todo_statistics.dto.TodoStatisticsAggregationRow;
import lv.ctco.springboottemplate.features.todo_statistics.dto.TodoStatisticsRequest;
import lv.ctco.springboottemplate.features.todo_statistics.dto.Todos;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Service
public class TodoStatisticsService {
  private final MongoTemplate mongoTemplate;

  private TodoStatisticsService(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  public TodoStatistics makeStatistics(TodoStatisticsRequest request) {
    setRequestParams(request);

    long total = 0;
    long completed = 0;
    long pending = 0;
    Map<String, Integer> userStats = new HashMap<>();
    Map<String, List<Todos>> todosMap = initiateTodosMap();

    TodoStatistics dto = new TodoStatistics(total, completed, pending, userStats);

    try {
      AggregationResults<TodoStatisticsAggregationRow> results = aggregateResults(request);

      for (TodoStatisticsAggregationRow r : results) {
        total += r.getCount();
        if (Boolean.TRUE.equals(r.getCompleted())) {
          completed += r.getCount();
          todosMap.get("completed").addAll(r.getTodos());
        } else {
          pending += r.getCount();
          todosMap.get("pending").addAll(r.getTodos());
        }
        userStats.merge(r.getUser(), Math.toIntExact(r.getCount()), Integer::sum);
      }

      dto.setTotalTodos(total);
      dto.setCompletedTodos(completed);
      dto.setPendingTodos(pending);
      dto.setUserStats(userStats);

      if (request.getFormat().equals(TodoStatisticsFormatEnum.detailed)) {
        dto.setTodos(todosMap);
      }

      return dto;

    } catch (Exception e) {
      throw new RuntimeException("Error while fetching statistics", e);
    }
  }

  private void setRequestParams(TodoStatisticsRequest request) {
    Instant from = request.getFrom();
    Instant to = request.getTo();

    Instant finalTo = (to != null) ? to : Instant.now();
    Instant finalFrom = (from != null) ? from : finalTo.minus(7, ChronoUnit.DAYS);

    TodoStatisticsFormatEnum format =
        Objects.requireNonNullElse(request.getFormat(), TodoStatisticsFormatEnum.summary);
    request.setFrom(finalFrom);
    request.setTo(finalTo);
    request.setFormat(format);
  }

  private Map<String, List<Todos>> initiateTodosMap() {
    Map<String, List<Todos>> todosMap = new HashMap<>();
    todosMap.put("completed", new ArrayList<>());
    todosMap.put("pending", new ArrayList<>());

    return todosMap;
  }

  private AggregationResults<TodoStatisticsAggregationRow> aggregateResults(
      TodoStatisticsRequest request) {

    Aggregation agg =
        Aggregation.newAggregation(
            Aggregation.match(
                Criteria.where("createdAt").gte(request.getFrom()).lte(request.getTo())),
            Aggregation.project("completed", "title", "createdBy", "createdAt", "completedAt"),
            Aggregation.group("completed", "createdBy")
                .count()
                .as("count")
                .push(
                    new BasicDBObject("_id", "$_id")
                        .append("title", "$title")
                        .append("createdBy", "$createdBy")
                        .append("createdAt", "$createdAt")
                        .append("completedAt", "$completedAt"))
                .as("todos"),
            Aggregation.project("count", "todos")
                .and("_id.completed")
                .as("completed")
                .and("_id.createdBy")
                .as("user"));

    return mongoTemplate.aggregate(agg, "todos", TodoStatisticsAggregationRow.class);
  }
}
