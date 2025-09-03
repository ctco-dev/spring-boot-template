package lv.ctco.springboottemplate.features.todo_statistics.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TodoStatisticsValidFormatValidator.class)
public @interface TodoStatisticsValidFormat {
  String message() default "must be any of {enumClass}";

  Class<? extends Enum<?>> enumClass();

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
