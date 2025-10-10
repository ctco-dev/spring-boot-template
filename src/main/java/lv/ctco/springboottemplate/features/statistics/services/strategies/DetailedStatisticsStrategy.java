package lv.ctco.springboottemplate.features.statistics.services.strategies;

import lv.ctco.springboottemplate.features.statistics.models.*;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
class DetailedStatisticsStrategy extends AbstractStatisticsStrategy {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_INSTANT;

    DetailedStatisticsStrategy(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }

    @Override
    public StatisticsFormat format() {
        return StatisticsFormat.DETAILED;
    }

    @Override
    public StatisticsResponse compute(StatisticsQuery query) {
        ProjectionOperation projectTodosFields = Aggregation.project("title", "createdBy", "createdAt", "updatedAt", "completed", "completedAt");
        Aggregation agg = Aggregation.newAggregation(
                matchDateFilter(query),
                Aggregation.facet(
                                Aggregation.group()
                                        .count().as("total")
                                        .sum(ConditionalOperators.when(Criteria.where("completed").is(true)).then(1).otherwise(0)).as("completed")
                        ).as("counts")
                        .and(Aggregation.group("createdBy").count().as("count")).as("userStats")
                        .and(Aggregation.match(Criteria.where("completed").is(true)), projectTodosFields).as("completedTodosSource")
                        .and(Aggregation.match(Criteria.where("completed").is(false)), projectTodosFields).as("pendingTodosSource")
        );

        Document root = aggregateSingle(agg);
        int total = 0;
        int completed = 0;
        if (root != null) {
            var counts = getArray(root, "counts");
            if (!counts.isEmpty()) {
                Document first = counts.getFirst();
                total = first.getInteger("total", 0);
                completed = first.getInteger("completed", 0);
            }
        }
        int pending = total - completed;

        var userStats = extractUserStats(root);

        List<TodosStatistics> completedTodos = Optional.ofNullable(root)
                .map(r -> getArray(r, "completedTodosSource")).orElse(List.of()).stream()
                .map(d -> toTodosStatistics(d, true))
                .collect(Collectors.toList());

        List<TodosStatistics> pendingTodos = Optional.ofNullable(root)
                .map(r -> getArray(r, "pendingTodosSource")).orElse(List.of()).stream()
                .map(d -> toTodosStatistics(d, false))
                .collect(Collectors.toList());

        return new StatisticsDetailedResponse(
                total,
                completed,
                pending,
                userStats,
                new TodosSection(completedTodos, pendingTodos)
        );
    }

    private TodosStatistics toTodosStatistics(Document d, boolean isCompleted) {
        String id = d.getObjectId("_id").toHexString();
        String title = d.getString("title");
        String createdBy = d.getString("createdBy");
        Instant createdAtInstant = toInstant(d.get("createdAt"));
        String createdAt = createdAtInstant == null ? null : ISO.format(createdAtInstant);
        Instant completedAt = isCompleted ? Optional.ofNullable(toInstant(d.get("completedAt")))
                .or(() -> Optional.ofNullable(toInstant(d.get("updatedAt"))))
                .orElse(null) : null;

        return new TodosStatistics(id, title, createdBy, createdAt, Optional.ofNullable(completedAt));
    }
}

