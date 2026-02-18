package lv.ctco.springboottemplate.features.statistics.models;

import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lv.ctco.springboottemplate.validation.statistics.ValidStatisticsRequest;

@ValidStatisticsRequest
public record StatisticsRequestDto(
    String from,
    String to,
    @Pattern(
            regexp = "^(summary|detailed)$",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "{statistics.format.invalid}")
        String format) {

  public LocalDate fromDate() {
    return from != null && !from.isBlank() ? LocalDate.parse(from) : null;
  }

  public LocalDate toDate() {
    return to != null && !to.isBlank() ? LocalDate.parse(to) : null;
  }

  public String normalizedFormat() {
    return format == null ? "summary" : format;
  }
}
