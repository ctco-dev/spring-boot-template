package lv.ctco.springboottemplate.features.todo_statistics.dto;

import io.swagger.v3.oas.annotations.Parameter;
import java.time.Instant;
import lv.ctco.springboottemplate.features.todo_statistics.TodoStatisticsFormatEnum;
import lv.ctco.springboottemplate.features.todo_statistics.validators.TodoStatisticsValidDateRange;

@TodoStatisticsValidDateRange
public class TodoStatisticsRequest {
  @Parameter(example = "2025-09-01T10:30:00Z")
  private Instant from;

  @Parameter(example = "2025-10-01T23:59:59Z")
  private Instant to;

  @Parameter(example = "summary")
  private TodoStatisticsFormatEnum format;

  public Instant getFrom() {
    return from;
  }

  public void setFrom(Instant from) {
    this.from = from;
  }

  public Instant getTo() {
    return to;
  }

  public void setTo(Instant to) {
    this.to = to;
  }

  public TodoStatisticsFormatEnum getFormat() {
    return format;
  }

  public void setFormat(TodoStatisticsFormatEnum format) {
    this.format = format;
  }
}
