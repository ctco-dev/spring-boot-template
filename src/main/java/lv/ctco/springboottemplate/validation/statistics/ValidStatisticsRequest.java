package lv.ctco.springboottemplate.validation.statistics;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidStatisticsRequestValidator.class)
public @interface ValidStatisticsRequest {
  String message() default "{statistics.request.invalid}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
