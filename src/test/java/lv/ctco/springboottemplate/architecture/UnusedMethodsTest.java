package lv.ctco.springboottemplate.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

public class UnusedMethodsTest {

  protected static JavaClasses importedClasses;

  @BeforeAll
  static void setup() {
    importedClasses = new ClassFileImporter().importPackages("lv.ctco.springboottemplate");
  }

  @Test
  void injectable_beans_should_not_have_unused_methods() {
    ArchRule rule =
        classes()
            .that()
            .areAnnotatedWith(Component.class)
            .or()
            .areAnnotatedWith(Service.class)
            .or()
            .areAnnotatedWith(Repository.class)
            .should(
                new ArchCondition<>("have all methods used") {
                  @Override
                  public void check(JavaClass item, ConditionEvents events) {
                    item.getMethods().stream()
                        .filter(method -> !isSpringLifecycleMethod(method))
                        .filter(method -> method.getAccessesToSelf().isEmpty())
                        .forEach(
                            method ->
                                events.add(
                                    new SimpleConditionEvent(
                                        method,
                                        false,
                                        String.format(
                                            "Method %s in %s is never used and should be deleted",
                                            method.getName(), item.getName()))));
                  }

                  private boolean isSpringLifecycleMethod(JavaMethod method) {
                    String name = method.getName();
                    JavaClass owner = method.getOwner();

                    return name.equals("init")
                        || name.equals("destroy")
                        || name.equals("afterPropertiesSet")
                        || name.startsWith("set")
                        || owner.isAssignableTo(Converter.class) && name.equals("convert");
                  }
                })
            .because(
                "All methods in Spring beans must be used somewhere in the codebase. "
                    + "Unused methods should be deleted to maintain clean code");

    rule.check(importedClasses);
  }
}
