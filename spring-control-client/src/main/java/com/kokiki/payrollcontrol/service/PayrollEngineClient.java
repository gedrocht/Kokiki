package com.kokiki.payrollcontrol.service;

import com.kokiki.payrollcontrol.model.CobolPayrollExecutionRequest;
import com.kokiki.payrollcontrol.model.CobolPayrollExecutionResponse;

/**
 * Contract implemented by payroll calculation engines.
 */
public interface PayrollEngineClient {

  /**
   * Performs a payroll calculation.
   *
   * @param payrollExecutionRequest normalized payroll input
   * @return payroll result
   */
  CobolPayrollExecutionResponse calculatePayroll(CobolPayrollExecutionRequest payrollExecutionRequest);

  /**
   * Returns the human-readable name of the active engine mode.
   *
   * @return engine mode name
   */
  String calculationEngineMode();
}
