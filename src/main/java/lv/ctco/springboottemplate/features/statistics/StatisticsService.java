package lv.ctco.springboottemplate.features.statistics;

import org.springframework.stereotype.Service;

@Service
public class StatisticsService {
  private final TodoStatsRepository todoStatsRepository;

  public StatisticsService(TodoStatsRepository todoRepository) {
    this.todoStatsRepository = todoRepository;
  }

  public TodoSummaryStatsDto getStatistics() {
      return todoStatsRepository.getStats();
  }
}
