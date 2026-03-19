package com.kokiki.payrollcontrol.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

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
}
