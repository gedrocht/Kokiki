package com.kokiki.payrollcontrol.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.kokiki.payrollcontrol.model.CobolPayrollExecutionRequest;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

/**
 * Integration test that talks to the real COBOL executable when CI provides it.
 */
@EnabledIfSystemProperty(named = "cobol.payroll.executable.path", matches = ".+")
class ProcessBackedCobolPayrollEngineClientIT {

  @Test
  void shouldCalculatePayrollThroughTheRealCobolExecutable() {
    final String cobolExecutablePath = System.getProperty("cobol.payroll.executable.path");
    final ProcessBackedCobolPayrollEngineClient processBackedCobolPayrollEngineClient =
        new ProcessBackedCobolPayrollEngineClient(cobolExecutablePath);

    final var payrollExecutionResponse = processBackedCobolPayrollEngineClient.calculatePayroll(
        new CobolPayrollExecutionRequest(
            "EMP-1001",
            "Adriana Bennett",
            new BigDecimal("42.50"),
            new BigDecimal("80.00"),
            new BigDecimal("6.00"),
            new BigDecimal("0.24"),
            new BigDecimal("185.00"),
            new BigDecimal("150.00")));

    assertThat(payrollExecutionResponse.grossPayAmount()).isEqualByComparingTo("3932.50");
    assertThat(payrollExecutionResponse.netPayAmount()).isEqualByComparingTo("2607.07");
  }
}
