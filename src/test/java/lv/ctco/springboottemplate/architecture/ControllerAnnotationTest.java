package lv.ctco.springboottemplate.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

public class ControllerAnnotationTest {

  protected static JavaClasses importedClasses;

  @BeforeAll
  static void setup() {
    importedClasses = new ClassFileImporter().importPackages("lv.ctco.springboottemplate");
  }

  @Test
  void controllers_should_be_annotated_properly() {
    ArchRule controllerAnnotationRule =
        classes()
            .that()
            .resideInAPackage("..features..")
            .and()
            .haveSimpleNameEndingWith("Controller")
            .should()
            .beAnnotatedWith(RestController.class)
            .andShould()
            .beAnnotatedWith(RequestMapping.class)
            .because(
                """
                                In Spring Boot, controllers must be annotated with @RestController and @RequestMapping
                                to properly expose REST APIs.

                                - @RestController marks the class as a REST endpoint: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/RestController.html
                                - @RequestMapping sets the base path for all endpoints in the controller: https://spring.io/guides/gs/rest-service/

                                This ensures consistency and discoverability of your API.
                                """);

    controllerAnnotationRule.check(importedClasses);
  }
}
