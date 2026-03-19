package com.kokiki.payrollcontrol.service;

import com.kokiki.payrollcontrol.model.CobolPayrollExecutionRequest;
import com.kokiki.payrollcontrol.model.CobolPayrollExecutionResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Local mirror of the COBOL formula used when the repository is explored
 * without a compiled COBOL executable.
 *
 * <p>This implementation exists so beginners can run the Java application
 * immediately while still keeping the real COBOL process integration available
 * for stricter environments.</p>
 */
@Service
@ConditionalOnProperty(name = "company-payroll.execution-mode", havingValue = "demonstration", matchIfMissing = true)
public final class DemonstrationPayrollEngineClient implements PayrollEngineClient {

  private static final Logger APPLICATION_LOGGER = LoggerFactory.getLogger(DemonstrationPayrollEngineClient.class);
  private static final BigDecimal OVERTIME_PAY_MULTIPLIER = new BigDecimal("1.50");
  private static final BigDecimal RETIREMENT_CONTRIBUTION_RATE = new BigDecimal("0.05");
  private static final BigDecimal PAID_LEAVE_ACCRUAL_PER_WORKED_HOUR = new BigDecimal("0.0385");

  /**
   * Creates the demonstration payroll engine client.
   *
   * <p>This explicit constructor keeps the class friendly to both Spring and
   * static-analysis rules.</p>
   */
  public DemonstrationPayrollEngineClient() {
  }

  @Override
  public CobolPayrollExecutionResponse calculatePayroll(final CobolPayrollExecutionRequest payrollExecutionRequest) {
    APPLICATION_LOGGER.warn(
        "Using the demonstration payroll engine mirror for employee {}.",
        payrollExecutionRequest.employeeIdentifier());

    final BigDecimal grossRegularPayAmount =
        scaleCurrency(payrollExecutionRequest.hourlyWageAmount().multiply(payrollExecutionRequest.regularHoursWorked()));
    final BigDecimal grossOvertimePayAmount =
        scaleCurrency(payrollExecutionRequest.hourlyWageAmount()
            .multiply(payrollExecutionRequest.overtimeHoursWorked())
            .multiply(OVERTIME_PAY_MULTIPLIER));
    final BigDecimal grossPayAmount =
        scaleCurrency(grossRegularPayAmount.add(grossOvertimePayAmount).add(payrollExecutionRequest.performanceBonusAmount()));
    final BigDecimal taxWithholdingAmount =
        scaleCurrency(grossPayAmount.multiply(payrollExecutionRequest.standardTaxRatePercentage()));
    final BigDecimal retirementContributionAmount = scaleCurrency(grossPayAmount.multiply(RETIREMENT_CONTRIBUTION_RATE));
    final BigDecimal paidLeaveAccruedHours =
        scaleHours(payrollExecutionRequest.regularHoursWorked()
            .add(payrollExecutionRequest.overtimeHoursWorked())
            .multiply(PAID_LEAVE_ACCRUAL_PER_WORKED_HOUR));
    final BigDecimal netPayAmount =
        scaleCurrency(grossPayAmount.subtract(taxWithholdingAmount)
            .subtract(retirementContributionAmount)
            .subtract(payrollExecutionRequest.benefitDeductionAmount()));

    return new CobolPayrollExecutionResponse(
        payrollExecutionRequest.employeeIdentifier(),
        payrollExecutionRequest.employeeFullName(),
        grossRegularPayAmount,
        grossOvertimePayAmount,
        grossPayAmount,
        taxWithholdingAmount,
        retirementContributionAmount,
        scaleCurrency(payrollExecutionRequest.benefitDeductionAmount()),
        paidLeaveAccruedHours,
        netPayAmount);
  }

  @Override
  public String calculationEngineMode() {
    return "demonstration";
  }

  /**
   * Normalizes currency values to two decimal places.
   *
   * @param value value to scale
   * @return scaled value
   */
  private BigDecimal scaleCurrency(final BigDecimal value) {
    return value.setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Normalizes hour-based values to two decimal places.
   *
   * @param value value to scale
   * @return scaled value
   */
  private BigDecimal scaleHours(final BigDecimal value) {
    return value.setScale(2, RoundingMode.HALF_UP);
  }
}
