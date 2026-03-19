package com.kokiki.payrollcontrol.model;

/**
 * Lightweight status response for operators and beginners exploring the system.
 *
 * @param companyName fictional company name
 * @param employeeCount number of seeded employees
 * @param calculationEngineMode currently active engine mode
 */
public record SystemStatusResponse(
    String companyName,
    int employeeCount,
    String calculationEngineMode) {
}
