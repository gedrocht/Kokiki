package com.kokiki.payrollcontrol.model;

import java.math.BigDecimal;

/**
 * Internal request sent to the COBOL-compatible payroll engine interface.
 *
 * @param employeeIdentifier employee identifier
 * @param employeeFullName employee full name
 * @param hourlyWageAmount hourly pay rate
 * @param regularHoursWorked regular hours
 * @param overtimeHoursWorked overtime hours
 * @param standardTaxRatePercentage simplified tax rate
 * @param benefitDeductionAmount fixed benefits deduction
 * @param performanceBonusAmount additional bonus amount
 */
public record CobolPayrollExecutionRequest(
    String employeeIdentifier,
    String employeeFullName,
    BigDecimal hourlyWageAmount,
    BigDecimal regularHoursWorked,
    BigDecimal overtimeHoursWorked,
    BigDecimal standardTaxRatePercentage,
    BigDecimal benefitDeductionAmount,
    BigDecimal performanceBonusAmount) {
}
