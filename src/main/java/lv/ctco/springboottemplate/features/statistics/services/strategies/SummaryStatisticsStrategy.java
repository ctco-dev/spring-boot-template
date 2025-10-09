package lv.ctco.springboottemplate.features.statistics.services.strategies;

import lv.ctco.springboottemplate.features.statistics.models.StatisticsFormat;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsQuery;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsResponse;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsSummaryResponse;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
class SummaryStatisticsStrategy extends AbstractStatisticsStrategy {

    SummaryStatisticsStrategy(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }

    @Override
    public StatisticsFormat format() {
        return StatisticsFormat.SUMMARY;
    }

    @Override
    public StatisticsResponse compute(StatisticsQuery query) {
        Aggregation agg = Aggregation.newAggregation(
                matchDateFilter(query),
                Aggregation.facet(
                                Aggregation.group()
                                        .count().as("total")
                                        .sum(ConditionalOperators.when(Criteria.where("completed").is(true)).then(1).otherwise(0)).as("completed")
                        ).as("counts")
                        .and(
                                Aggregation.group("createdBy").count().as("count")
                        ).as("userStats")
        );

        Document root = aggregateSingle(agg);
        int total = 0;
        int completed = 0;
        if (root != null) {
            List<Document> counts = getArray(root, "counts");
            if (!counts.isEmpty()) {
                Document first = counts.get(0);
                total = first.getInteger("total", 0);
                completed = first.getInteger("completed", 0);
            }
        }
        int pending = total - completed;
        Map<String, Integer> userStats = extractUserStats(root);

        return new StatisticsSummaryResponse(total, completed, pending, userStats);
    }
}

