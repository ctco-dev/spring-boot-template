package lv.ctco.springboottemplate.features.statistics;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lv.ctco.springboottemplate.features.statistics.dto.StatsFormatEnum;
import lv.ctco.springboottemplate.features.statistics.dto.TodoBreakdownDTO;
import lv.ctco.springboottemplate.features.statistics.dto.TodoItemDTO;
import lv.ctco.springboottemplate.features.statistics.dto.TodoStatsResponseDTO;
import lv.ctco.springboottemplate.features.todo.Todo;
import lv.ctco.springboottemplate.features.todo.TodoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsService {

  private final MongoTemplate mongoTemplate;
  private final TodoRepository todoRepository;

  public TodoStatsResponseDTO query(
      Optional<LocalDate> from, Optional<LocalDate> to, StatsFormatEnum format) {
    Criteria criteria = Criteria.where("createdAt");
    ;

    if (from.isPresent()) criteria = criteria.gte(from.get());
    if (to.isPresent()) criteria = criteria.lte(to.get());

    Query query = new Query(criteria);

    List<Todo> todos = mongoTemplate.find(query, Todo.class);

    return buildSummaryFromTodos(todos, format);
  }

  private TodoStatsResponseDTO buildSummaryFromTodos(List<Todo> todos, StatsFormatEnum format) {
    int total = todos.size();
    int completed = (int) todos.stream().filter(Todo::completed).count();
    int pending = total - completed;

    List<TodoItemDTO> completedList =
        todos.stream().filter(Todo::completed).map(this::toDto).toList();

    List<TodoItemDTO> pendingList =
        todos.stream().filter(t -> !t.completed()).map(this::toDto).toList();

    Map<String, Integer> userStats =
        todos.stream()
            .collect(Collectors.groupingBy(Todo::createdBy, Collectors.summingInt(t -> 1)));

    TodoBreakdownDTO breakdown = new TodoBreakdownDTO(completedList, pendingList);
    return new TodoStatsResponseDTO(
        total,
        completed,
        pending,
        userStats,
        format == StatsFormatEnum.DETAILED ? Optional.of(breakdown) : Optional.empty());
  }

  private TodoItemDTO toDto(Todo todo) {
    return new TodoItemDTO(
        todo.id(),
        todo.title(),
        todo.createdBy(),
        todo.createdAt(),
        todo.completed() ? todo.updatedAt() : null);
  }
}
