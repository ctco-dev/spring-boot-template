package lv.ctco.springboottemplate.features.statistics;

import lv.ctco.springboottemplate.features.statistics.dto.*;
import lv.ctco.springboottemplate.features.todo.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TodoStatsRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public TodoSummaryStatsDto getStats() {
        long total = mongoTemplate.count(new Query(), Todo.class);
        long completed = mongoTemplate.count(Query.query(Criteria.where("completed").is(true)), Todo.class);
        long pending = total - completed;

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.group("createdBy").count().as("count")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(agg, "todos", Document.class);
        Map<String, Long> userStats = new HashMap<>();
        for (Document doc : results.getMappedResults()) {
            userStats.put(doc.getString("_id"), doc.getInteger("count").longValue());
        }

        return new TodoSummaryStatsDto(total, completed, pending, userStats);
    }

    public TodoDetailedStatsDto getExpandedStats() {
        TodoSummaryStatsDto summaryStats = getStats();

        Aggregation aggregateCompleted = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("completed").is(true)),
                Aggregation.project()
                        .and("_id").as("id")
                        .and("title").as("title")
                        .and("createdBy").as("createdBy")
                        .and("createdAt").as("createdAt")
                        .and("updatedAt").as("completedAt")
        );

        List<CompletedTodoDto> completedTodos = mongoTemplate
                .aggregate(aggregateCompleted, "todos", CompletedTodoDto.class)
                .getMappedResults();

        Query queryPending = new Query(Criteria.where("completed").is(false));
        queryPending.fields()
                .include("id")
                .include("title")
                .include("createdBy")
                .include("createdAt");

        List<PendingTodoDto> pendingTodos = mongoTemplate.find(queryPending, PendingTodoDto.class, "todos");

        return new TodoDetailedStatsDto(
                summaryStats.totalTodos(),
                summaryStats.completedTodos(),
                summaryStats.pendingTodos(),
                summaryStats.userStats(),
                new TodoDetailsDto(completedTodos, pendingTodos)
        );
    }
}
