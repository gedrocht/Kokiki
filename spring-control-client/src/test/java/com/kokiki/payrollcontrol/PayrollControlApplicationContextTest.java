package com.kokiki.payrollcontrol;

import static org.assertj.core.api.Assertions.assertThat;

import com.kokiki.payrollcontrol.service.EmployeePayrollApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Starts the Spring application context to ensure the main wiring is healthy.
 */
@SpringBootTest
class PayrollControlApplicationContextTest {

  @Autowired
  private EmployeePayrollApplicationService employeePayrollApplicationService;

  @Test
  void shouldLoadTheApplicationContext() {
    assertThat(employeePayrollApplicationService).isNotNull();
  }
}
