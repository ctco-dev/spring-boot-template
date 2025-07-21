package lv.ctco.springboottemplate.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PackageAccessTest {

  protected static JavaClasses importedClasses;

  @BeforeAll
  static void setup() {
    importedClasses =
        new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("lv.ctco.springboottemplate.features");
  }

  @Test
  void repositories_should_not_be_accessed_from_other_features() {
    Set<String> featurePackages =
        importedClasses.stream()
            .map(cls -> cls.getPackageName())
            .filter(p -> p.contains(".features."))
            .map(p -> p.split("\\.features\\.")[1].split("\\.")[0])
            .collect(Collectors.toSet());

    for (String feature : featurePackages) {
      String featurePkg = "..features." + feature + "..";
      ArchRule rule =
          noClasses()
              .that()
              .resideOutsideOfPackage(featurePkg)
              .should()
              .accessClassesThat()
              .resideInAPackage(featurePkg)
              .andShould()
              .accessClassesThat()
              .haveSimpleNameEndingWith("Repository")
              .because(
                  "Repositories in '"
                      + feature
                      + "' should only be used within their own feature. "
                      + "To access data from other features, use their Service classes instead.");

      rule.check(importedClasses);
    }
  }
}
