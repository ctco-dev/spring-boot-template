package lv.ctco.springboottemplate.features.statistics;

import java.time.Instant;
import java.util.Optional;

public record CompactTodo(
    String id, String title, String createdBy, Instant createdAt, Optional<Instant> completedAt) {}
