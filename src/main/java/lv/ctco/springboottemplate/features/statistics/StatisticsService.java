package lv.ctco.springboottemplate.features.statistics;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

  private final StatisticsAggregationService statisticsAggregationService;

  public StatisticsService(StatisticsAggregationService statisticsAggregationService) {
    this.statisticsAggregationService = statisticsAggregationService;
  }

  public Statistics getStatistics(GetStatisticsParams params) {
    validateDateRange(params.from(), params.to());

    StatisticsAggregationResult aggregationResult =
        statisticsAggregationService.getAllStatistics(params.from(), params.to());

    Map<String, Long> userStats =
        aggregationResult.userStats().stream()
            .collect(Collectors.toMap(UserStatsResult::userId, UserStatsResult::count));

    Optional<TodoStatistics> todoDetails =
        params.format() == StatisticsFormat.DETAILED
            ? Optional.of(
                new TodoStatistics(
                    aggregationResult.completedTodos(), aggregationResult.pendingTodos()))
            : Optional.empty();

    return new Statistics(
        aggregationResult.basicStats().totalTodos(),
        aggregationResult.basicStats().completedTodos(),
        aggregationResult.basicStats().pendingTodos(),
        userStats,
        todoDetails);
  }

  private void validateDateRange(Optional<LocalDate> from, Optional<LocalDate> to) {
    if (from.isPresent() && to.isPresent()) {
      if (from.get().isAfter(to.get())) {
        throw new IllegalArgumentException("From date must be before or equal to to date");
      }
    }
  }
}
