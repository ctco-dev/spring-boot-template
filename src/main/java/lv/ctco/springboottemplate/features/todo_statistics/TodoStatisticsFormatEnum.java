package lv.ctco.springboottemplate.features.todo_statistics;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TodoStatisticsFormatEnum {
  summary,
  detailed;

  @JsonCreator
  public static TodoStatisticsFormatEnum fromString(String value) {
    return TodoStatisticsFormatEnum.valueOf(value.toLowerCase());
  }
}
