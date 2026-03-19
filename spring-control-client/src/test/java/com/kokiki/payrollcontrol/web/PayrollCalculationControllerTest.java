package com.kokiki.payrollcontrol.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kokiki.payrollcontrol.model.EmployeeRecord;
import com.kokiki.payrollcontrol.model.PayrollCalculationResponse;
import com.kokiki.payrollcontrol.model.SystemStatusResponse;
import com.kokiki.payrollcontrol.service.EmployeeRecordNotFoundException;
import com.kokiki.payrollcontrol.service.EmployeePayrollApplicationService;
import com.kokiki.payrollcontrol.service.PayrollProcessingException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Web-layer tests that verify the public HTTP contract.
 */
@WebMvcTest({
    PayrollCalculationController.class,
    EmployeeDirectoryController.class,
    SystemStatusController.class
})
@Import(RestExceptionHandler.class)
class PayrollCalculationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private EmployeePayrollApplicationService employeePayrollApplicationService;

  @Test
  void shouldReturnTheCalculatedPayrollResponse() throws Exception {
    when(employeePayrollApplicationService.calculatePayroll(any()))
        .thenReturn(new PayrollCalculationResponse(
            "EMP-1001",
            "Adriana Bennett",
            new BigDecimal("3400.00"),
            new BigDecimal("382.50"),
            new BigDecimal("3932.50"),
            new BigDecimal("943.80"),
            new BigDecimal("196.63"),
            new BigDecimal("185.00"),
            new BigDecimal("3.31"),
            new BigDecimal("2607.07"),
            "demonstration"));

    mockMvc.perform(post("/application-programming-interface/payroll/calculations")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "employeeIdentifier": "EMP-1001",
                  "regularHoursWorked": 80.0,
                  "overtimeHoursWorked": 6.0,
                  "performanceBonusAmount": 150.00
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.employeeIdentifier").value("EMP-1001"))
        .andExpect(jsonPath("$.grossPayAmount").value(3932.50))
        .andExpect(jsonPath("$.calculationEngineMode").value("demonstration"));
  }

  @Test
  void shouldReturnValidationErrorsForInvalidRequests() throws Exception {
    mockMvc.perform(post("/application-programming-interface/payroll/calculations")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "employeeIdentifier": "",
                  "regularHoursWorked": -1.0,
                  "overtimeHoursWorked": -1.0,
                  "performanceBonusAmount": -1.0
                }
                """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCategory").value("VALIDATION_FAILURE"))
        .andExpect(jsonPath("$.details.length()").value(4));
  }

  @Test
  void shouldReturnNotFoundWhenTheEmployeeDoesNotExist() throws Exception {
    when(employeePayrollApplicationService.calculatePayroll(any()))
        .thenThrow(new EmployeeRecordNotFoundException("No employee record exists for identifier EMP-9999."));

    mockMvc.perform(post("/application-programming-interface/payroll/calculations")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "employeeIdentifier": "EMP-9999",
                  "regularHoursWorked": 80.0,
                  "overtimeHoursWorked": 6.0,
                  "performanceBonusAmount": 150.00
                }
                """))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCategory").value("EMPLOYEE_NOT_FOUND"));
  }

  @Test
  void shouldReturnInternalServerErrorWhenPayrollProcessingFails() throws Exception {
    when(employeePayrollApplicationService.calculatePayroll(any()))
        .thenThrow(new PayrollProcessingException("The COBOL payroll engine could not be reached."));

    mockMvc.perform(post("/application-programming-interface/payroll/calculations")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "employeeIdentifier": "EMP-1001",
                  "regularHoursWorked": 80.0,
                  "overtimeHoursWorked": 6.0,
                  "performanceBonusAmount": 150.00
                }
                """))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.errorCategory").value("PAYROLL_PROCESSING_FAILURE"));
  }

  @Test
  void shouldListEmployeesAndStatus() throws Exception {
    when(employeePayrollApplicationService.findAllEmployeeRecords())
        .thenReturn(List.of(new EmployeeRecord(
            "EMP-1001",
            "Adriana Bennett",
            "Payroll",
            "Payroll Manager",
            new BigDecimal("42.50"),
            true,
            new BigDecimal("0.24"),
            new BigDecimal("185.00"))));
    when(employeePayrollApplicationService.systemStatus())
        .thenReturn(new SystemStatusResponse("Blue Sky Office Design", 30, "demonstration"));

    mockMvc.perform(get("/application-programming-interface/employees"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].employeeFullName").value("Adriana Bennett"));

    mockMvc.perform(get("/application-programming-interface/system/status"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.companyName").value("Blue Sky Office Design"))
        .andExpect(jsonPath("$.employeeCount").value(30));
  }
}
