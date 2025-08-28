package lv.ctco.springboottemplate.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FeatureNamingConventionTest {

  protected static JavaClasses importedClasses;

  @BeforeAll
  static void setup() {
    importedClasses = new ClassFileImporter().importPackages("lv.ctco.springboottemplate");
  }

  @Test
  void only_validly_named_top_level_classes_should_exist_in_features_package() {
    ArchRule rule =
        classes()
            .that()
            .resideInAPackage("..features..")
            .and()
            .haveNameNotMatching(".*\\$.*") // ← exclude inner classes
            .should()
            .haveSimpleNameEndingWith("Controller")
            .orShould()
            .haveSimpleNameEndingWith("Service")
            .orShould()
            .haveSimpleNameEndingWith("Repository")
            .orShould()
            .haveSimpleNameStartingWith("Todo")
            .orShould()
            .haveSimpleNameStartingWith("Greeting")
            .orShould()
            .haveSimpleNameStartingWith("Statistics")
            .because(
                """
            The 'features' package should contain only top-level components like:
            - *Controller
            - *Service
            - *Repository
            - *Todo

            Reference:
            https://www.baeldung.com/spring-component-repository-service

            Inner classes (like CreateTodoRequest) are ignored,
            since they're scoped to their parent and follow a different purpose.
            """);

    rule.check(importedClasses);
  }
}
