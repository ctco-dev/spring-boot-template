package lv.ctco.springboottemplate.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.mapping.Document;

public class DocumentTypeTest {
  protected static JavaClasses importedClasses;

  @BeforeAll
  static void setup() {
    importedClasses =
        new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("lv.ctco.springboottemplate.features");
  }

  @Test
  void document_entities_should_be_records() {
    ArchRule rule =
        classes()
            .that()
            .areAnnotatedWith(Document.class)
            .should()
            .beRecords()
            .because(
                "MongoDB document entities should use records instead of simple POJO with Lombock annotated classes");

    rule.check(importedClasses);
  }
}
