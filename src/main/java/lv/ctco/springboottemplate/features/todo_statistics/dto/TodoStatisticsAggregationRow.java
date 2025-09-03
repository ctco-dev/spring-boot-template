package lv.ctco.springboottemplate.features.todo_statistics.dto;

import java.util.ArrayList;
import java.util.List;

public class TodoStatisticsAggregationRow {
  private String user;
  private Boolean completed;
  private long count;
  private List<Todos> todos;

  public TodoStatisticsAggregationRow() {}

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public Boolean getCompleted() {
    return completed;
  }

  public void setCompleted(Boolean completed) {
    this.completed = completed;
  }

  public long getCount() {
    return count;
  }

  public void setCount(long count) {
    this.count = count;
  }

  public List<Todos> getTodos() {
    return todos;
  }

  public void setTodos(ArrayList<Todos> todos) {
    this.todos = todos;
  }
}
