package lv.ctco.springboottemplate.features.statistics;

import java.time.LocalDate;
import java.util.Optional;

public record GetStatisticsParams(
    Optional<LocalDate> from, Optional<LocalDate> to, StatisticsFormat format) {}
