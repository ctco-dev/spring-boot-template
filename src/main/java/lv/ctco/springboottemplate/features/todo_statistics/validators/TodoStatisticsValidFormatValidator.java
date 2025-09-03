package lv.ctco.springboottemplate.features.todo_statistics.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class TodoStatisticsValidFormatValidator
    implements ConstraintValidator<TodoStatisticsValidFormat, String> {
  private Class<? extends Enum<?>> enumClass;

  @Override
  public void initialize(TodoStatisticsValidFormat constraintAnnotation) {
    this.enumClass = constraintAnnotation.enumClass();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) return true;
    return Arrays.stream(enumClass.getEnumConstants())
        .anyMatch(e -> e.name().equalsIgnoreCase(value));
  }
}
