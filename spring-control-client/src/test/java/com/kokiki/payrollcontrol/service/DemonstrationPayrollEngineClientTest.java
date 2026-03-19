package com.kokiki.payrollcontrol.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.kokiki.payrollcontrol.model.CobolPayrollExecutionRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

/**
 * Verifies that the demonstration formula stays aligned with the documented
 * payroll rules.
 */
class DemonstrationPayrollEngineClientTest {

  private final DemonstrationPayrollEngineClient demonstrationPayrollEngineClient =
      new DemonstrationPayrollEngineClient();

  @Test
  void shouldCalculatePayrollUsingTheExpectedBusinessFormula() {
    final CobolPayrollExecutionRequest payrollExecutionRequest = new CobolPayrollExecutionRequest(
        "EMP-1001",
        "Adriana Bennett",
        new BigDecimal("42.50"),
        new BigDecimal("80.00"),
        new BigDecimal("6.00"),
        new BigDecimal("0.24"),
        new BigDecimal("185.00"),
        new BigDecimal("150.00"));

    final var payrollExecutionResponse =
        demonstrationPayrollEngineClient.calculatePayroll(payrollExecutionRequest);

    assertThat(payrollExecutionResponse.grossRegularPayAmount()).isEqualByComparingTo("3400.00");
    assertThat(payrollExecutionResponse.grossOvertimePayAmount()).isEqualByComparingTo("382.50");
    assertThat(payrollExecutionResponse.grossPayAmount()).isEqualByComparingTo("3932.50");
    assertThat(payrollExecutionResponse.taxWithholdingAmount()).isEqualByComparingTo("943.80");
    assertThat(payrollExecutionResponse.retirementContributionAmount()).isEqualByComparingTo("196.63");
    assertThat(payrollExecutionResponse.paidLeaveAccruedHours()).isEqualByComparingTo("3.31");
    assertThat(payrollExecutionResponse.netPayAmount()).isEqualByComparingTo("2607.07");
  }
}
