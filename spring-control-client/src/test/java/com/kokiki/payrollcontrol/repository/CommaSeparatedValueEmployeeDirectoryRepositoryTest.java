package com.kokiki.payrollcontrol.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.kokiki.payrollcontrol.model.EmployeeRecord;
import com.kokiki.payrollcontrol.service.PayrollProcessingException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.ByteArrayResource;

/**
 * Verifies that the seed employee directory is loaded correctly.
 */
class CommaSeparatedValueEmployeeDirectoryRepositoryTest {

  @Test
  void shouldLoadAllThirtyEmployeesFromTheSeedDirectory() {
    final CommaSeparatedValueEmployeeDirectoryRepository employeeDirectoryRepository =
        new CommaSeparatedValueEmployeeDirectoryRepository(new DefaultResourceLoader());

    assertThat(employeeDirectoryRepository.findAllEmployeeRecords()).hasSize(30);
    assertThat(employeeDirectoryRepository.findEmployeeRecordByEmployeeIdentifier("EMP-1001"))
        .isPresent()
        .get()
        .extracting(employeeRecord -> employeeRecord.employeeFullName())
        .isEqualTo("Adriana Bennett");
  }

  @Test
  void shouldReturnAnImmutableCopyOfTheEmployeeDirectory() {
    final CommaSeparatedValueEmployeeDirectoryRepository employeeDirectoryRepository =
        new CommaSeparatedValueEmployeeDirectoryRepository(new DefaultResourceLoader());

    final List<EmployeeRecord> loadedEmployeeRecords = employeeDirectoryRepository.findAllEmployeeRecords();

    assertThatThrownBy(() -> loadedEmployeeRecords.add(sampleEmployeeRecord()))
        .isInstanceOf(UnsupportedOperationException.class);
    assertThat(employeeDirectoryRepository.findAllEmployeeRecords()).hasSize(30);
  }

  @Test
  void shouldFailFastWhenTheEmployeeDirectoryContainsOnlyAHeader() {
    final ResourceLoader resourceLoader =
        fixedTextResourceLoader("employeeIdentifier,employeeFullName,departmentName,jobTitle,hourlyWageAmount,"
            + "eligibleForOvertime,standardTaxRatePercentage,benefitDeductionAmount");

    assertThatThrownBy(() -> new CommaSeparatedValueEmployeeDirectoryRepository(resourceLoader))
        .isInstanceOf(PayrollProcessingException.class)
        .hasMessageContaining("did not contain employee data");
  }

  @Test
  void shouldFailFastWhenAnEmployeeDirectoryLineHasTheWrongNumberOfColumns() {
    final ResourceLoader resourceLoader =
        fixedTextResourceLoader("employeeIdentifier,employeeFullName,departmentName,jobTitle,hourlyWageAmount,"
            + "eligibleForOvertime,standardTaxRatePercentage,benefitDeductionAmount\n"
            + "EMP-1001,Adriana Bennett,Payroll,Payroll Manager,42.50,true,0.24");

    assertThatThrownBy(() -> new CommaSeparatedValueEmployeeDirectoryRepository(resourceLoader))
        .isInstanceOf(PayrollProcessingException.class)
        .hasMessageContaining("Expected 8 columns");
  }

  private ResourceLoader fixedTextResourceLoader(final String employeeDirectoryContents) {
    return new ResourceLoader() {
      @Override
      public Resource getResource(final String resourceLocation) {
        return byteArrayResource(employeeDirectoryContents);
      }

      @Override
      public ClassLoader getClassLoader() {
        return CommaSeparatedValueEmployeeDirectoryRepositoryTest.class.getClassLoader();
      }
    };
  }

  private Resource byteArrayResource(final String employeeDirectoryContents) {
    return new ByteArrayResource(employeeDirectoryContents.getBytes(StandardCharsets.UTF_8));
  }

  private EmployeeRecord sampleEmployeeRecord() {
    return new EmployeeRecord(
        "EMP-9001",
        "Morgan Reyes",
        "Finance",
        "Controller",
        new BigDecimal("55.00"),
        true,
        new BigDecimal("0.25"),
        new BigDecimal("210.00"));
  }
}
