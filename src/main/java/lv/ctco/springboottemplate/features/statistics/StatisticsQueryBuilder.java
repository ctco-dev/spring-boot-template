package lv.ctco.springboottemplate.features.statistics;

import lv.ctco.springboottemplate.features.statistics.models.StatisticsFormat;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsQuery;
import lv.ctco.springboottemplate.features.statistics.models.StatisticsRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class StatisticsQueryBuilder {
    public StatisticsQuery build(StatisticsRequest request) {
        List<String> errors = new ArrayList<>();
        LocalDate from = parseDate("from", request.from(), errors);
        LocalDate to = parseDate("to", request.to(), errors);

        if (from != null && to != null && from.isAfter(to)) {
            errors.add("Parameter 'from' must be before or equal to 'to'.");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }

        StatisticsFormat format = StatisticsQuery.parseFormat(request.format());
        return new StatisticsQuery(from, to, format);
    }

    private LocalDate parseDate(String name, String raw, List<String> errors) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(raw);
        } catch (DateTimeParseException e) {
            errors.add("Invalid '" + name + "' date: " + raw);
            return null;
        }
    }
}