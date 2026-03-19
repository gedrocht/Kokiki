package com.kokiki.payrollcontrol.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kokiki.payrollcontrol.model.CobolPayrollExecutionResponse;
import com.kokiki.payrollcontrol.model.EmployeeRecord;
import com.kokiki.payrollcontrol.model.PayrollCalculationResponse;
import com.kokiki.payrollcontrol.model.SystemStatusResponse;
import com.kokiki.payrollcontrol.repository.EmployeeDirectoryRepository;
import com.kokiki.payrollcontrol.service.EmployeePayrollApplicationService;
import com.kokiki.payrollcontrol.service.PayrollEngineClient;
import com.kokiki.payrollcontrol.service.PayrollProcessingException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Web-layer tests that verify the public HTTP contract.
 *
 * <p>These tests intentionally use standalone MockMvc instead of a sliced
 * Spring application context so they remain stable across newer Java releases
 * without relying on final-class mocking.</p>
 */
class PayrollCalculationControllerTest {

  private MockMvc mockMvc;
  private EmployeeDirectoryRepository employeeDirectoryRepository;
  private PayrollEngineClient payrollEngineClient;

  @BeforeEach
  void setUp() {
    employeeDirectoryRepository = mock(EmployeeDirectoryRepository.class);
    payrollEngineClient = mock(PayrollEngineClient.class);

    final EmployeePayrollApplicationService employeePayrollApplicationService =
        new EmployeePayrollApplicationService(employeeDirectoryRepository, payrollEngineClient);
    final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    validator.afterPropertiesSet();

    mockMvc = MockMvcBuilders.standaloneSetup(
            new PayrollCalculationController(employeePayrollApplicationService),
            new EmployeeDirectoryController(employeePayrollApplicationService),
            new SystemStatusController(employeePayrollApplicationService))
        .setControllerAdvice(new RestExceptionHandler())
        .setMessageConverters(new MappingJackson2HttpMessageConverter())
        .setValidator(validator)
        .build();
  }

  @Test
  void shouldReturnTheCalculatedPayrollResponse() throws Exception {
    when(employeeDirectoryRepository.findEmployeeRecordByEmployeeIdentifier("EMP-1001"))
        .thenReturn(Optional.of(sampleEmployeeRecord()));
    when(payrollEngineClient.calculatePayroll(any()))
        .thenReturn(new CobolPayrollExecutionResponse(
            "EMP-1001",
            "Adriana Bennett",
            new BigDecimal("3400.00"),
            new BigDecimal("382.50"),
            new BigDecimal("3932.50"),
            new BigDecimal("943.80"),
            new BigDecimal("196.63"),
            new BigDecimal("185.00"),
            new BigDecimal("3.31"),
            new BigDecimal("2607.07")));
    when(payrollEngineClient.calculationEngineMode()).thenReturn("demonstration");

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
    when(employeeDirectoryRepository.findEmployeeRecordByEmployeeIdentifier("EMP-9999"))
        .thenReturn(Optional.empty());

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
    when(employeeDirectoryRepository.findEmployeeRecordByEmployeeIdentifier("EMP-1001"))
        .thenReturn(Optional.of(sampleEmployeeRecord()));
    when(payrollEngineClient.calculatePayroll(any()))
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
    when(employeeDirectoryRepository.findAllEmployeeRecords()).thenReturn(List.of(sampleEmployeeRecord()));
    when(payrollEngineClient.calculationEngineMode()).thenReturn("demonstration");

    mockMvc.perform(get("/application-programming-interface/employees"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].employeeFullName").value("Adriana Bennett"));

    mockMvc.perform(get("/application-programming-interface/system/status"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.companyName").value("Blue Sky Office Design"))
        .andExpect(jsonPath("$.employeeCount").value(1));
  }

  @Test
  void shouldExposeStatusResponseAsExpected() {
    final SystemStatusResponse systemStatusResponse =
        new SystemStatusResponse("Blue Sky Office Design", 30, "demonstration");

    org.assertj.core.api.Assertions.assertThat(systemStatusResponse.companyName())
        .isEqualTo("Blue Sky Office Design");
    org.assertj.core.api.Assertions.assertThat(systemStatusResponse.employeeCount())
        .isEqualTo(30);
    org.assertj.core.api.Assertions.assertThat(systemStatusResponse.calculationEngineMode())
        .isEqualTo("demonstration");
  }

  @Test
  void shouldExposePayrollCalculationResponseAsExpected() {
    final PayrollCalculationResponse payrollCalculationResponse = new PayrollCalculationResponse(
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
        "demonstration");

    org.assertj.core.api.Assertions.assertThat(payrollCalculationResponse.netPayAmount())
        .isEqualByComparingTo("2607.07");
    org.assertj.core.api.Assertions.assertThat(payrollCalculationResponse.calculationEngineMode())
        .isEqualTo("demonstration");
  }

  private EmployeeRecord sampleEmployeeRecord() {
    return new EmployeeRecord(
        "EMP-1001",
        "Adriana Bennett",
        "Payroll",
        "Payroll Manager",
        new BigDecimal("42.50"),
        true,
        new BigDecimal("0.24"),
        new BigDecimal("185.00"));
  }
}
