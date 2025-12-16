package lv.ctco.springboottemplate.features.statistics.services.strategies;

import lv.ctco.springboottemplate.features.statistics.models.*;
import lv.ctco.springboottemplate.features.statistics.services.StatisticsRepository;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static lv.ctco.springboottemplate.features.statistics.StatisticsAggregationUtil.extractUserStats;
import static lv.ctco.springboottemplate.features.statistics.StatisticsAggregationUtil.getArray;

@Component
class DetailedStatisticsStrategy implements StatisticsComputationStrategy {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_INSTANT;
    private final StatisticsRepository repository;

    DetailedStatisticsStrategy(StatisticsRepository repository) {
        this.repository = repository;
    }

    @Override
    public StatisticsFormat format() {
        return StatisticsFormat.DETAILED;
    }

    @Override
    public StatisticsResponse compute(StatisticsQuery query) {
        Document root = repository.executeDetailed(query);
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
        Map<String, Integer> userStats = extractUserStats(root);

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

    private Instant toInstant(Object obj) {
        if (obj instanceof java.util.Date date) return date.toInstant();
        if (obj instanceof Instant inst) return inst;
        return null;
    }
}
