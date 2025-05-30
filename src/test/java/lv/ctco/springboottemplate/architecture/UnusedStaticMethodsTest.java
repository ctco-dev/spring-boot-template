package lv.ctco.springboottemplate.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

public class UnusedStaticMethodsTest {

  protected static JavaClasses importedClasses;

  @BeforeAll
  static void setup() {
    importedClasses =
        new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("lv.ctco.springboottemplate.features");
  }

  @Test
  void non_beans_should_not_have_unused_non_private_static_methods() {
    ArchRule rule =
        classes()
            .that()
            .areNotAnnotatedWith(Component.class)
            .and()
            .areNotAnnotatedWith(Service.class)
            .and()
            .areNotAnnotatedWith(Repository.class)
            .and()
            .areNotAnnotatedWith(RestController.class)
            .should(
                new ArchCondition<>("have all non-private static methods used") {
                  @Override
                  public void check(JavaClass item, ConditionEvents events) {
                    item.getMethods().stream()
                        .filter(method -> method.getModifiers().contains(JavaModifier.STATIC))
                        .filter(method -> !method.getModifiers().contains(JavaModifier.PRIVATE))
                        .filter(method -> method.getAccessesToSelf().isEmpty())
                        .forEach(
                            method ->
                                events.add(
                                    new SimpleConditionEvent(
                                        method,
                                        false,
                                        String.format(
                                            "Static method %s in %s is not private but never used",
                                            method.getName(), item.getName()))));
                  }
                })
            .because(
                "Non-private static methods in utility classes should be used somewhere in the codebase");

    rule.check(importedClasses);
  }
}
