package lv.ctco.springboottemplate.features.statistics;

import lv.ctco.springboottemplate.features.statistics.dto.TodoDetailedStatsDto;
import lv.ctco.springboottemplate.features.statistics.dto.TodoSummaryStatsDto;
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

    public TodoDetailedStatsDto getExpandedStatistics() {
        return todoStatsRepository.getExpandedStats();
    }
}
