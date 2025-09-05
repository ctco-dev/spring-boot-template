package lv.ctco.springboottemplate.features.todo_statistics.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Instant;
import lv.ctco.springboottemplate.features.todo_statistics.dto.TodoStatisticsRequest;

public class TodoStatisticsDateRangeValidator
    implements ConstraintValidator<TodoStatisticsValidDateRange, TodoStatisticsRequest> {

  @Override
  public boolean isValid(TodoStatisticsRequest request, ConstraintValidatorContext context) {
    Instant from = request.getFrom();
    Instant to = request.getTo();

    if (from == null && to == null) {
      return false;
    }

    if (from != null && to != null) {
      return !from.isAfter(to);
    }

    return true;
  }
}
