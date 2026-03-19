package com.kokiki.payrollcontrol.web;

import com.kokiki.payrollcontrol.model.PayrollCalculationRequest;
import com.kokiki.payrollcontrol.model.PayrollCalculationResponse;
import com.kokiki.payrollcontrol.service.EmployeePayrollApplicationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that accepts payroll calculation requests.
 */
@RestController
@RequestMapping("/application-programming-interface/payroll/calculations")
public final class PayrollCalculationController {

  private final EmployeePayrollApplicationService employeePayrollApplicationService;

  /**
   * Creates the controller.
   *
   * @param employeePayrollApplicationService central payroll application service
   */
  public PayrollCalculationController(final EmployeePayrollApplicationService employeePayrollApplicationService) {
    this.employeePayrollApplicationService = employeePayrollApplicationService;
  }

  /**
   * Calculates payroll for the provided request.
   *
   * @param payrollCalculationRequest validated request body
   * @return payroll calculation response
   */
  @PostMapping
  public PayrollCalculationResponse calculatePayroll(
      @Valid @RequestBody final PayrollCalculationRequest payrollCalculationRequest) {
    return employeePayrollApplicationService.calculatePayroll(payrollCalculationRequest);
  }
}
