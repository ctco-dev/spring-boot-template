package lv.ctco.springboottemplate.features.statistics.models;

import java.time.Instant;

public record StatisticsTodoItemDto(
    String id, String title, String createdBy, Instant createdAt, Instant completedAt) {}
