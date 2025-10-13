package lv.ctco.springboottemplate.features.statistics;

public enum ResponseFormat {
    SUMMARY("summary"),
    DETAILED("detailed");

    private final String value;

    ResponseFormat(String format) {
        this.value = format;
    }

    public String getValue() {
        return value;
    }
}
