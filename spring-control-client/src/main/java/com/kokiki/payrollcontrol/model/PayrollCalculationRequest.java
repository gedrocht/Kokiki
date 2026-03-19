package com.kokiki.payrollcontrol.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Request body used by payroll staff to ask for a payroll calculation.
 *
 * @param employeeIdentifier employee that the calculation should be run for
 * @param regularHoursWorked number of non-overtime hours
 * @param overtimeHoursWorked number of overtime hours
 * @param performanceBonusAmount optional bonus for the payroll period
 */
public record PayrollCalculationRequest(
    @NotBlank(message = "Employee identifier is required.")
    String employeeIdentifier,

    @NotNull(message = "Regular hours worked is required.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Regular hours worked must be zero or greater.")
    BigDecimal regularHoursWorked,

    @NotNull(message = "Overtime hours worked is required.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Overtime hours worked must be zero or greater.")
    BigDecimal overtimeHoursWorked,

    @NotNull(message = "Performance bonus amount is required.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Performance bonus amount must be zero or greater.")
    BigDecimal performanceBonusAmount) {
}
