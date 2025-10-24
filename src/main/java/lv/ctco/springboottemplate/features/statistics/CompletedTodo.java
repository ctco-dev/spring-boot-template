package lv.ctco.springboottemplate.features.statistics;

import java.time.Instant;

public record CompletedTodo(
        String id,
        String title,
        String createdBy,
        Instant createdAt,
        Instant completedAt
) {}
