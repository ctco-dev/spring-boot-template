package lv.ctco.springboottemplate.features.todo;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Domain model representing a single Todo item.
 *
 * <p>This class is a {@link java.lang.Record} which makes it immutable and concise. It is annotated
 * with {@link org.springframework.data.mongodb.core.mapping.Document} to map it to a MongoDB
 * document.
 *
 * <p>It includes metadata like createdBy, updatedBy, createdAt, updatedAt — useful for auditing.
 *
 * <p>📚 Learn more:
 *
 * <ul>
 *   <li><a
 *       href="https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mapping">Spring
 *       Data MongoDB Mapping</a>
 *   <li><a href="https://docs.oracle.com/en/java/javase/21/language/records.html">Java Records
 *       (Oracle Docs)</a>
 * </ul>
 */
@Document(collection = "todos")
public record Todo(
    @Id String id,
    String title,
    String description,
    boolean completed,
    String createdBy,
    String updatedBy,
    Instant createdAt,
    Instant updatedAt) {}
