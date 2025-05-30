package lv.ctco.springboottemplate.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Repository;

public class RepositoryInterfaceTest {

  protected static JavaClasses importedClasses;

  @BeforeAll
  static void setup() {
    importedClasses = new ClassFileImporter().importPackages("lv.ctco.springboottemplate");
  }

  @Test
  void repositories_should_be_interfaces() {
    ArchRule rule =
        classes()
            .that()
            .areAnnotatedWith(Repository.class)
            .should()
            .beInterfaces()
            .because(
                "Repositories should be interfaces to promote loose coupling and allow for multiple implementations");

    rule.check(importedClasses);
  }
}
