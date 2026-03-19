package com.kokiki.payrollcontrol.web;

import com.kokiki.payrollcontrol.model.SystemStatusResponse;
import com.kokiki.payrollcontrol.service.EmployeePayrollApplicationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that exposes a small operational status payload.
 */
@RestController
@RequestMapping("/application-programming-interface/system/status")
public final class SystemStatusController {

  private final EmployeePayrollApplicationService employeePayrollApplicationService;

  /**
   * Creates the controller.
   *
   * @param employeePayrollApplicationService central payroll application service
   */
  public SystemStatusController(final EmployeePayrollApplicationService employeePayrollApplicationService) {
    this.employeePayrollApplicationService = employeePayrollApplicationService;
  }

  /**
   * Returns the system status payload.
   *
   * @return status response
   */
  @GetMapping
  public SystemStatusResponse status() {
    return employeePayrollApplicationService.systemStatus();
  }
}
