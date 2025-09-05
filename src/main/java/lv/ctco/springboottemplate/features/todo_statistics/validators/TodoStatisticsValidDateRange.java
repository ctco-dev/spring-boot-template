package lv.ctco.springboottemplate.features.todo_statistics.validators;

import static lv.ctco.springboottemplate.errorhandling.ErrorMessages.TODO_STATS_DATE_RANGE_INVALID;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TodoStatisticsDateRangeValidator.class)
@Documented
public @interface TodoStatisticsValidDateRange {
  String message() default TODO_STATS_DATE_RANGE_INVALID;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
