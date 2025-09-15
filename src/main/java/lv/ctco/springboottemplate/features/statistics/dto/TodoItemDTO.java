package lv.ctco.springboottemplate.features.statistics.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TodoItemDTO(
    String id, String title, String createdBy, Instant createdAt, Instant completedAt) {}
