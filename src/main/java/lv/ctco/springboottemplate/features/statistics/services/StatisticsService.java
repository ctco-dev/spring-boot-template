package lv.ctco.springboottemplate.features.statistics.services;

import lv.ctco.springboottemplate.features.statistics.StatisticsQueryBuilder;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsFormat;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsQuery;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsRequest;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsResponse;
import lv.ctco.springboottemplate.features.statistics.services.strategies.StatisticsComputationStrategy;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {
    private final StatisticsQueryBuilder queryBuilder;
    private final Map<StatisticsFormat, StatisticsComputationStrategy> strategies = new EnumMap<>(StatisticsFormat.class);

    public StatisticsService(List<StatisticsComputationStrategy> strategyImplementations, StatisticsQueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;

        for (StatisticsComputationStrategy s : strategyImplementations) {
            strategies.put(s.format(), s);
        }
    }

    public StatisticsResponse computeStatistics(StatisticsRequest request) {
        StatisticsQuery query = queryBuilder.build(request);
        StatisticsComputationStrategy strategy = strategies.get(query.format());
        if (strategy == null) {
            throw new IllegalStateException("No strategy registered for format: " + query.format());
        }

        return strategy.compute(query);
    }
}
