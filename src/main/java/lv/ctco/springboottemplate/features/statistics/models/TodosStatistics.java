package lv.ctco.springboottemplate.features.statistics.models;

import java.time.Instant;
import java.util.Optional;

public record TodosStatistics(
        String id,
        String title,
        String createdBy,
        String createdAt,
        Optional<Instant> completedAt) {
}