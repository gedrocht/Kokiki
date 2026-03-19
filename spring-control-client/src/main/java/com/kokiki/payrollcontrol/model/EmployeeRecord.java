package com.kokiki.payrollcontrol.model;

import java.math.BigDecimal;

/**
 * Represents one employee in the fictional Blue Sky Office Design directory.
 *
 * @param employeeIdentifier unique payroll-facing identifier
 * @param employeeFullName complete employee name as shown to payroll staff
 * @param departmentName department that the employee belongs to
 * @param roleTitle descriptive role title
 * @param hourlyWageAmount base hourly pay rate
 * @param overtimeEligible whether the employee is allowed to earn overtime pay
 * @param standardTaxRatePercentage simplified demonstration tax rate
 * @param benefitDeductionAmount fixed deduction for benefits per payroll run
 */
public record EmployeeRecord(
    String employeeIdentifier,
    String employeeFullName,
    String departmentName,
    String roleTitle,
    BigDecimal hourlyWageAmount,
    boolean overtimeEligible,
    BigDecimal standardTaxRatePercentage,
    BigDecimal benefitDeductionAmount) {
}
