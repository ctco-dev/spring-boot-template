package lv.ctco.springboottemplate.features.statistics;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.format.annotation.DateTimeFormat;

public record StatisticsInputParams(
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> from,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> to,
    @StatisticsFormat String format) {}
