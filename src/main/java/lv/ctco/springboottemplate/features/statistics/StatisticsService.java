package lv.ctco.springboottemplate.features.statistics;

import java.time.Instant;
import lv.ctco.springboottemplate.features.statistics.dto.TodoStatsDto;
import org.springframework.stereotype.Service;

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
