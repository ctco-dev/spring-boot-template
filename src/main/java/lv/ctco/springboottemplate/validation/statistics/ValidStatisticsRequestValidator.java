package lv.ctco.springboottemplate.validation.statistics;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsRequestDto;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class ValidStatisticsRequestValidator
    implements ConstraintValidator<ValidStatisticsRequest, StatisticsRequestDto> {

  private final MessageSource messageSource;

  public ValidStatisticsRequestValidator(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public boolean isValid(StatisticsRequestDto value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }

    LocalDate fromDate = parseDate(value.from(), "from", context);
    LocalDate toDate = parseDate(value.to(), "to", context);

    if (fromDate == null && hasValue(value.from())) {
      return false;
    }
    if (toDate == null && hasValue(value.to())) {
      return false;
    }

    if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
      addViolation(context, message("statistics.range.invalid"), "from");
      return false;
    }

    return true;
  }

  private LocalDate parseDate(String raw, String field, ConstraintValidatorContext context) {
    if (!hasValue(raw)) {
      return null;
    }

    try {
      return LocalDate.parse(raw);
    } catch (DateTimeParseException ex) {
      addViolation(context, message("statistics.date.invalid", field), field);
      return null;
    }
  }

  private void addViolation(
      ConstraintValidatorContext context, String message, String propertyName) {
    context.disableDefaultConstraintViolation();
    context
        .buildConstraintViolationWithTemplate(message)
        .addPropertyNode(propertyName)
        .addConstraintViolation();
  }

  private boolean hasValue(String value) {
    return value != null && !value.isBlank();
  }

  private String message(String code, Object... args) {
    return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
  }
}
