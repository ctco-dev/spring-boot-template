package lv.ctco.springboottemplate.features.statistics.services.strategies;

import lv.ctco.springboottemplate.features.statistics.models.StatisticsFormat;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsQuery;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsResponse;

public interface StatisticsComputationStrategy {
    StatisticsFormat format();

    StatisticsResponse compute(StatisticsQuery query);
}

