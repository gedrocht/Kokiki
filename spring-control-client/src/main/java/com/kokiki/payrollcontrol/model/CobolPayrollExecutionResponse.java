package com.kokiki.payrollcontrol.model;

import java.math.BigDecimal;

/**
 * Internal payroll result produced by either the real COBOL process or the
 * demonstration mirror implementation.
 *
 * @param employeeIdentifier employee identifier
 * @param employeeFullName employee full name
 * @param grossRegularPayAmount regular pay amount
 * @param grossOvertimePayAmount overtime pay amount
 * @param grossPayAmount total gross pay
 * @param taxWithholdingAmount tax withholding
 * @param retirementContributionAmount retirement contribution
 * @param benefitDeductionAmount benefit deduction
 * @param paidLeaveAccruedHours paid leave accrual
 * @param netPayAmount net pay
 */
public record CobolPayrollExecutionResponse(
    String employeeIdentifier,
    String employeeFullName,
    BigDecimal grossRegularPayAmount,
    BigDecimal grossOvertimePayAmount,
    BigDecimal grossPayAmount,
    BigDecimal taxWithholdingAmount,
    BigDecimal retirementContributionAmount,
    BigDecimal benefitDeductionAmount,
    BigDecimal paidLeaveAccruedHours,
    BigDecimal netPayAmount) {
}
