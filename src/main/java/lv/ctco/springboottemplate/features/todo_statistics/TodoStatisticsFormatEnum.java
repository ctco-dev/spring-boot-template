package lv.ctco.springboottemplate.features.todo_statistics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TodoStatisticsFormatEnum {
  SUMMARY("summary"),
  DETAILED("detailed");

  private final String value;

  TodoStatisticsFormatEnum(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static TodoStatisticsFormatEnum fromString(String value) {
    for (TodoStatisticsFormatEnum format : TodoStatisticsFormatEnum.values()) {
      if (format.value.equalsIgnoreCase(value)) {
        return format;
      }
    }
    throw new IllegalArgumentException(
        "Unknown format: " + value + "must be summary, detailed or empty");
  }
}
