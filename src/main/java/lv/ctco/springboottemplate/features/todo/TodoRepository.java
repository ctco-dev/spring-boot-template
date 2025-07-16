package lv.ctco.springboottemplate.features.todo;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Todo} persistence.
 *
 * <p>Extends {@link org.springframework.data.mongodb.repository.MongoRepository} to inherit common
 * MongoDB data access methods like save, findAll, findById, etc.
 *
 * <p>Spring automatically creates a proxy implementation at runtime and registers this interface as
 * a bean due to the {@link org.springframework.stereotype.Repository} annotation.
 *
 * <p>This interface is part of the Data Access Layer (DAL) in the typical layered architecture.
 *
 * <p>📚 Learn more:
 *
 * <ul>
 *   <li><a href="https://www.baeldung.com/spring-data-mongodb-tutorial">Baeldung: Spring Data
 *       MongoDB</a>
 *   <li><a href="https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/">Spring
 *       Data MongoDB Reference</a>
 * </ul>
 */
@Repository
public interface TodoRepository extends MongoRepository<Todo, String> {
  List<Todo> findByTitleContainingIgnoreCase(String title);
}
