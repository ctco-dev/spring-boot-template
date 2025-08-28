package lv.ctco.springboottemplate.features.statistics;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StatisticsFormatValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StatisticsFormat {
  String message() default "Either 'summary' or 'detailed' must be provided for format";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
