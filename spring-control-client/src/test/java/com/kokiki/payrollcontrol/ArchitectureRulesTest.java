package com.kokiki.payrollcontrol;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Architecture rules keep package dependencies readable as the project grows.
 */
@AnalyzeClasses(packages = "com.kokiki.payrollcontrol", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureRulesTest {

  @ArchTest
  static final ArchRule webLayerShouldNotDependDirectlyOnRepositoryLayer =
      classes()
          .that().resideInAPackage("..web..")
          .should().onlyDependOnClassesThat()
          .resideOutsideOfPackage("..repository..");

  @ArchTest
  static final ArchRule serviceLayerShouldNotDependOnWebLayer =
      classes()
          .that().resideInAPackage("..service..")
          .should().onlyDependOnClassesThat()
          .resideOutsideOfPackage("..web..");
}
