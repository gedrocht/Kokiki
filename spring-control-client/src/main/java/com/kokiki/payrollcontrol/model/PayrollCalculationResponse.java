package com.kokiki.payrollcontrol.model;

import java.math.BigDecimal;

/**
 * Structured payroll result returned to the REST client.
 *
 * @param employeeIdentifier employee identifier that was processed
 * @param employeeFullName employee name that payroll staff should recognize
 * @param grossRegularPayAmount pay earned from regular hours
 * @param grossOvertimePayAmount pay earned from overtime hours
 * @param grossPayAmount total pay before deductions
 * @param taxWithholdingAmount simplified tax withholding amount
 * @param retirementContributionAmount retirement contribution amount
 * @param benefitDeductionAmount fixed benefit deduction amount
 * @param paidLeaveAccruedHours leave accrued during this payroll run
 * @param netPayAmount final take-home pay after deductions
 * @param calculationEngineMode tells the caller whether COBOL process mode or
 *     the local demonstration mirror was used
 */
public record PayrollCalculationResponse(
    String employeeIdentifier,
    String employeeFullName,
    BigDecimal grossRegularPayAmount,
    BigDecimal grossOvertimePayAmount,
    BigDecimal grossPayAmount,
    BigDecimal taxWithholdingAmount,
    BigDecimal retirementContributionAmount,
    BigDecimal benefitDeductionAmount,
    BigDecimal paidLeaveAccruedHours,
    BigDecimal netPayAmount,
    String calculationEngineMode) {
}
