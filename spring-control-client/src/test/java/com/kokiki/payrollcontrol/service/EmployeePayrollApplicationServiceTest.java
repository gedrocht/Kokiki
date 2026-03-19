package com.kokiki.payrollcontrol.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.kokiki.payrollcontrol.model.CobolPayrollExecutionRequest;
import com.kokiki.payrollcontrol.model.CobolPayrollExecutionResponse;
import com.kokiki.payrollcontrol.model.EmployeeRecord;
import com.kokiki.payrollcontrol.model.PayrollCalculationRequest;
import com.kokiki.payrollcontrol.repository.EmployeeDirectoryRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Verifies the orchestration layer between directory lookup and engine calls.
 */
class EmployeePayrollApplicationServiceTest {

  @Test
  void shouldMapThePayrollEngineResultIntoTheRestResponseShape() {
    final EmployeePayrollApplicationService employeePayrollApplicationService = new EmployeePayrollApplicationService(
        singleEmployeeDirectoryRepository(),
        new SuccessfulStubPayrollEngineClient());

    final var payrollCalculationResponse = employeePayrollApplicationService.calculatePayroll(
        new PayrollCalculationRequest(
            "EMP-1001",
            new BigDecimal("80.00"),
            new BigDecimal("6.00"),
            new BigDecimal("150.00")));

    assertThat(payrollCalculationResponse.employeeIdentifier()).isEqualTo("EMP-1001");
    assertThat(payrollCalculationResponse.grossPayAmount()).isEqualByComparingTo("3932.50");
    assertThat(payrollCalculationResponse.calculationEngineMode()).isEqualTo("stubbed-process");
  }

  @Test
  void shouldThrowWhenTheEmployeeIdentifierDoesNotExist() {
    final EmployeePayrollApplicationService employeePayrollApplicationService = new EmployeePayrollApplicationService(
        singleEmployeeDirectoryRepository(),
        new SuccessfulStubPayrollEngineClient());

    assertThatThrownBy(() -> employeePayrollApplicationService.calculatePayroll(
        new PayrollCalculationRequest(
            "EMP-9999",
            new BigDecimal("80.00"),
            new BigDecimal("6.00"),
            new BigDecimal("150.00"))))
        .isInstanceOf(EmployeeRecordNotFoundException.class)
        .hasMessageContaining("EMP-9999");
  }

  private EmployeeDirectoryRepository singleEmployeeDirectoryRepository() {
    final EmployeeRecord employeeRecord = new EmployeeRecord(
        "EMP-1001",
        "Adriana Bennett",
        "Payroll",
        "Payroll Manager",
        new BigDecimal("42.50"),
        true,
        new BigDecimal("0.24"),
        new BigDecimal("185.00"));

    return new EmployeeDirectoryRepository() {
      @Override
      public List<EmployeeRecord> findAllEmployeeRecords() {
        return List.of(employeeRecord);
      }

      @Override
      public Optional<EmployeeRecord> findEmployeeRecordByEmployeeIdentifier(final String employeeIdentifier) {
        return employeeRecord.employeeIdentifier().equals(employeeIdentifier)
            ? Optional.of(employeeRecord)
            : Optional.empty();
      }
    };
  }

  /**
   * Simple stub that lets the service test focus on orchestration instead of
   * formula details.
   */
  private static final class SuccessfulStubPayrollEngineClient implements PayrollEngineClient {

    @Override
    public CobolPayrollExecutionResponse calculatePayroll(final CobolPayrollExecutionRequest payrollExecutionRequest) {
      return new CobolPayrollExecutionResponse(
          payrollExecutionRequest.employeeIdentifier(),
          payrollExecutionRequest.employeeFullName(),
          new BigDecimal("3400.00"),
          new BigDecimal("382.50"),
          new BigDecimal("3932.50"),
          new BigDecimal("943.80"),
          new BigDecimal("196.63"),
          new BigDecimal("185.00"),
          new BigDecimal("3.31"),
          new BigDecimal("2607.07"));
    }

    @Override
    public String calculationEngineMode() {
      return "stubbed-process";
    }
  }
}
