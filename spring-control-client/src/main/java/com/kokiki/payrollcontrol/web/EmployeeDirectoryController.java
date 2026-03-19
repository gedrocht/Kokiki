package com.kokiki.payrollcontrol.web;

import com.kokiki.payrollcontrol.model.EmployeeRecord;
import com.kokiki.payrollcontrol.service.EmployeePayrollApplicationService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that exposes the employee directory.
 */
@RestController
@RequestMapping("/application-programming-interface/employees")
public final class EmployeeDirectoryController {

  private final EmployeePayrollApplicationService employeePayrollApplicationService;

  /**
   * Creates the controller.
   *
   * @param employeePayrollApplicationService central payroll application service
   */
  public EmployeeDirectoryController(final EmployeePayrollApplicationService employeePayrollApplicationService) {
    this.employeePayrollApplicationService = employeePayrollApplicationService;
  }

  /**
   * Returns the employee directory.
   *
   * @return employee list
   */
  @GetMapping
  public List<EmployeeRecord> listEmployeeDirectory() {
    return employeePayrollApplicationService.findAllEmployeeRecords();
  }
}
