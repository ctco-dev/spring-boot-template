package lv.ctco.springboottemplate.features.statistics.models;

import java.time.LocalDate;

public record StatisticsQuery(
        LocalDate from,
        LocalDate to,
        StatisticsFormat format
) {

    public StatisticsQuery {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("Parameter 'from' must be before or equal to 'to'.");
        }
    }

    public static StatisticsFormat parseFormat(String value) {
        if (value == null) {
            return StatisticsFormat.SUMMARY;
        }

        if (value.trim().equalsIgnoreCase("detailed")) {
            return StatisticsFormat.DETAILED;
        }

        return StatisticsFormat.SUMMARY;
    }
}
