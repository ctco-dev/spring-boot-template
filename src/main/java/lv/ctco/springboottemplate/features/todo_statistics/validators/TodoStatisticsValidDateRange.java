package lv.ctco.springboottemplate.features.todo_statistics.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TodoStatisticsDateRangeValidator.class)
@Documented
public @interface TodoStatisticsValidDateRange {
  String message() default
      "Either from, to, or both can pe provided. 'from' date must be before or equal to 'to' date";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
