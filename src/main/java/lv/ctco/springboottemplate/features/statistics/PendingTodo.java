package lv.ctco.springboottemplate.features.statistics;

import java.time.Instant;

public record PendingTodo(
        String id,
        String title,
        String createdBy,
        Instant createdAt
) {}
