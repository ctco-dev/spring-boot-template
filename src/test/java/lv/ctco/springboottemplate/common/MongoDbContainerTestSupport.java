package lv.ctco.springboottemplate.common;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public abstract class MongoDbContainerTestSupport {

  protected static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.8");

  static {
    mongoDBContainer.start();
  }

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }
}
