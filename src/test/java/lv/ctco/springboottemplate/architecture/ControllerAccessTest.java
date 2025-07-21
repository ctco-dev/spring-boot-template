package lv.ctco.springboottemplate.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ControllerAccessTest {

  protected static JavaClasses importedClasses;

  @BeforeAll
  static void setup() {
    importedClasses = new ClassFileImporter().importPackages("lv.ctco.springboottemplate");
  }

  @Test
  void controllers_should_not_access_repositories_directly() {
    ArchRule rule =
        noClasses()
            .that()
            .resideInAPackage("..features..")
            .and()
            .haveSimpleNameEndingWith("Controller")
            .should()
            .accessClassesThat()
            .haveSimpleNameEndingWith("Repository")
            .because(
                "Controllers should delegate data access to services, not repositories directly.");

    rule.check(importedClasses);
  }
}
