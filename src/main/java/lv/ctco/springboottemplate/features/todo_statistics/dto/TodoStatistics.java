package lv.ctco.springboottemplate.features.todo_statistics.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lv.ctco.springboottemplate.features.todo.Todo;

/** Statistics Data Transfer Object for {@link Todo} items */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TodoStatistics {
  @Schema(description = "Total number of todo items")
  private Long totalTodos;

  @Schema(description = "Number of completed items")
  private Long completedTodos;

  @Schema(description = "Number of pending items")
  private Long pendingTodos;

  @Schema(description = "Total number of todos per user")
  private Map<String, Integer> userStats;

  @Schema(description = "Detailed todos grouped by status")
  private Map<String, List<Todos>> todos;

  public TodoStatistics(long total, long completed, long pending, Map<String, Integer> userStats) {
    this.totalTodos = total;
    this.completedTodos = completed;
    this.pendingTodos = pending;
    this.userStats = userStats;
  }

  public Long getTotalTodos() {
    return totalTodos;
  }

  public void setTotalTodos(Long totalTodos) {
    this.totalTodos = totalTodos;
  }

  public Long getCompletedTodos() {
    return completedTodos;
  }

  public void setCompletedTodos(Long completedTodos) {
    this.completedTodos = completedTodos;
  }

  public Long getPendingTodos() {
    return pendingTodos;
  }

  public void setPendingTodos(Long pendingTodos) {
    this.pendingTodos = pendingTodos;
  }

  public Map<String, Integer> getUserStats() {
    return userStats;
  }

  public void setUserStats(Map<String, Integer> userStats) {
    this.userStats = userStats;
  }

  public Map<String, List<Todos>> getTodos() {
    return todos;
  }

  public void setTodos(Map<String, List<Todos>> todos) {
    this.todos = todos;
  }
}
