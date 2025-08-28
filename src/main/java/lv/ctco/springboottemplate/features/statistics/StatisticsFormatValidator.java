package lv.ctco.springboottemplate.features.statistics;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class StatisticsFormatValidator implements ConstraintValidator<StatisticsFormat, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return value.equals("summary") || value.equals("detailed");
  }
}
