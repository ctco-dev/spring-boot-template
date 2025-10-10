package lv.ctco.springboottemplate.features.statistics.services.strategies;

import lv.ctco.springboottemplate.features.statistics.models.StatisticsFormat;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsQuery;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsResponse;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsSummaryResponse;
import lv.ctco.springboottemplate.features.statistics.services.StatisticsRepository;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static lv.ctco.springboottemplate.features.statistics.StatisticsAggregationUtil.extractUserStats;
import static lv.ctco.springboottemplate.features.statistics.StatisticsAggregationUtil.getArray;

@Component
class SummaryStatisticsStrategy implements StatisticsComputationStrategy {

    private final StatisticsRepository repository;

    SummaryStatisticsStrategy(StatisticsRepository repository) {
        this.repository = repository;
    }

    @Override
    public StatisticsFormat format() {
        return StatisticsFormat.SUMMARY;
    }

    @Override
    public StatisticsResponse compute(StatisticsQuery query) {
        Document root = repository.executeSummary(query);
        int total = 0;
        int completed = 0;
        if (root != null) {
            List<Document> counts = getArray(root, "counts");
            if (!counts.isEmpty()) {
                Document first = counts.getFirst();
                total = first.getInteger("total", 0);
                completed = first.getInteger("completed", 0);
            }
        }
        int pending = total - completed;
        Map<String, Integer> userStats = extractUserStats(root);
        return new StatisticsSummaryResponse(total, completed, pending, userStats);
    }
}
