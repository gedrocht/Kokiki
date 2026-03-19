package com.kokiki.payrollcontrol.service;

import com.kokiki.payrollcontrol.model.CobolPayrollExecutionRequest;
import com.kokiki.payrollcontrol.model.CobolPayrollExecutionResponse;
import com.kokiki.payrollcontrol.model.EmployeeRecord;
import com.kokiki.payrollcontrol.model.PayrollCalculationRequest;
import com.kokiki.payrollcontrol.model.PayrollCalculationResponse;
import com.kokiki.payrollcontrol.model.SystemStatusResponse;
import com.kokiki.payrollcontrol.repository.EmployeeDirectoryRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Orchestrates employee lookup and payroll calculation.
 *
 * <p>This service is the central bridge between the REST layer and the payroll
 * engine abstraction.</p>
 */
@Service
public final class EmployeePayrollApplicationService {

  private static final Logger applicationLogger = LoggerFactory.getLogger(EmployeePayrollApplicationService.class);
  private static final String FICTIONAL_COMPANY_NAME = "Blue Sky Office Design";

  private final EmployeeDirectoryRepository employeeDirectoryRepository;
  private final PayrollEngineClient payrollEngineClient;

  /**
   * Creates the application service.
   *
   * @param employeeDirectoryRepository employee directory storage
   * @param payrollEngineClient payroll engine client
   */
  public EmployeePayrollApplicationService(
      final EmployeeDirectoryRepository employeeDirectoryRepository,
      final PayrollEngineClient payrollEngineClient) {
    this.employeeDirectoryRepository = employeeDirectoryRepository;
    this.payrollEngineClient = payrollEngineClient;
  }

  /**
   * Returns all employees.
   *
   * @return employee list
   */
  public List<EmployeeRecord> findAllEmployeeRecords() {
    return employeeDirectoryRepository.findAllEmployeeRecords();
  }

  /**
   * Calculates payroll for one employee request.
   *
   * @param payrollCalculationRequest request from the REST layer
   * @return structured payroll response
   */
  public PayrollCalculationResponse calculatePayroll(final PayrollCalculationRequest payrollCalculationRequest) {
    final EmployeeRecord employeeRecord = employeeDirectoryRepository
        .findEmployeeRecordByEmployeeIdentifier(payrollCalculationRequest.employeeIdentifier())
        .orElseThrow(() -> new EmployeeRecordNotFoundException(
            "No employee record exists for identifier " + payrollCalculationRequest.employeeIdentifier() + "."));

    applicationLogger.info("Calculating payroll for employee {}.", employeeRecord.employeeIdentifier());

    final CobolPayrollExecutionRequest payrollExecutionRequest = new CobolPayrollExecutionRequest(
        employeeRecord.employeeIdentifier(),
        employeeRecord.employeeFullName(),
        employeeRecord.hourlyWageAmount(),
        payrollCalculationRequest.regularHoursWorked(),
        payrollCalculationRequest.overtimeHoursWorked(),
        employeeRecord.standardTaxRatePercentage(),
        employeeRecord.benefitDeductionAmount(),
        payrollCalculationRequest.performanceBonusAmount());

    final CobolPayrollExecutionResponse payrollExecutionResponse =
        payrollEngineClient.calculatePayroll(payrollExecutionRequest);

    return new PayrollCalculationResponse(
        payrollExecutionResponse.employeeIdentifier(),
        payrollExecutionResponse.employeeFullName(),
        payrollExecutionResponse.grossRegularPayAmount(),
        payrollExecutionResponse.grossOvertimePayAmount(),
        payrollExecutionResponse.grossPayAmount(),
        payrollExecutionResponse.taxWithholdingAmount(),
        payrollExecutionResponse.retirementContributionAmount(),
        payrollExecutionResponse.benefitDeductionAmount(),
        payrollExecutionResponse.paidLeaveAccruedHours(),
        payrollExecutionResponse.netPayAmount(),
        payrollEngineClient.calculationEngineMode());
  }

  /**
   * Creates a small operational status object.
   *
   * @return status response
   */
  public SystemStatusResponse systemStatus() {
    return new SystemStatusResponse(
        FICTIONAL_COMPANY_NAME,
        employeeDirectoryRepository.findAllEmployeeRecords().size(),
        payrollEngineClient.calculationEngineMode());
  }
}
