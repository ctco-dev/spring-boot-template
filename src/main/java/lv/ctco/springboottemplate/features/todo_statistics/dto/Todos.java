package lv.ctco.springboottemplate.features.todo_statistics.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import lv.ctco.springboottemplate.features.todo.Todo;

/** Detailed Statistics Data Transfer Object for {@link Todo} items */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Todos(
    String id, String title, String createdBy, Instant createdAt, Instant completedAt) {}
