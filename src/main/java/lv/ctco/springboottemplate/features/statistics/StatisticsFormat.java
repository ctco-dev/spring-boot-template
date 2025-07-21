package lv.ctco.springboottemplate.features.statistics;

public enum StatisticsFormat {
  SUMMARY("summary"),
  DETAILED("detailed");

  private final String value;

  StatisticsFormat(String value) {
    this.value = value;
  }
}
