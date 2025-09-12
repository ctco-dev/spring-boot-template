package lv.ctco.springboottemplate.features.statistics;

import java.time.Instant;
import java.util.Optional;
import org.springframework.data.annotation.Id;

public record StatisticsTodo(
    @Id String id,
    String title,
    String createdBy,
    Instant createdAt,
    Optional<Instant> completedAt) {}
