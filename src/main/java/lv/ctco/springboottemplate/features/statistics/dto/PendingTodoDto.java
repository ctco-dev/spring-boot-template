package lv.ctco.springboottemplate.features.statistics.dto;

import java.time.Instant;

public record PendingTodoDto(String id, String title, String createdBy, Instant createdAt) {}
