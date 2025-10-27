package lv.ctco.springboottemplate.features.statistics;

import lv.ctco.springboottemplate.features.statistics.dto.TodoStatsDto;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class StatisticsService {
  private final TodoStatsRepository todoStatsRepository;

  public StatisticsService(TodoStatsRepository todoRepository) {
    this.todoStatsRepository = todoRepository;
  }

  public TodoStatsDto getStatistics(ResponseFormat format, Instant from, Instant to) {
      return todoStatsRepository.getStats(format, from, to);
  }
}
