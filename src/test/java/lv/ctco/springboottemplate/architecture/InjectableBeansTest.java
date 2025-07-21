package lv.ctco.springboottemplate.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

public class InjectableBeansTest {

  protected static JavaClasses importedClasses;

  @BeforeAll
  static void setup() {
    importedClasses =
        new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("lv.ctco.springboottemplate.features");
  }

  @Test
  void injectable_beans_should_use_constructor_injection() {
    // Rule 1: No @Autowired fields allowed
    ArchRule noFieldInjection =
        noFields()
            .should()
            .beAnnotatedWith(Autowired.class)
            .because("Field injection is discouraged, use constructor injection instead");

    // Rule 2: Spring components should have final fields
    ArchRule finalFieldsRule =
        classes()
            .that()
            .areAnnotatedWith(Component.class)
            .or()
            .areAnnotatedWith(Service.class)
            .or()
            .areAnnotatedWith(Repository.class)
            .or()
            .areAnnotatedWith(RestController.class)
            .should()
            .haveOnlyFinalFields()
            .because("Spring beans should use constructor injection and immutable fields");

    noFieldInjection.check(importedClasses);
    finalFieldsRule.check(importedClasses);
  }
}
