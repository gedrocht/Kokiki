package com.kokiki.payrollcontrol.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.kokiki.payrollcontrol.model.CobolPayrollExecutionRequest;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Verifies the branch-heavy behavior of the process-backed payroll client
 * without requiring a real COBOL compiler during unit tests.
 */
class ProcessBackedCobolPayrollEngineClientTest {

  @Test
  void shouldRejectBlankExecutablePaths() {
    assertThatThrownBy(() -> new ProcessBackedCobolPayrollEngineClient("   "))
        .isInstanceOf(PayrollProcessingException.class)
        .hasMessageContaining("company-payroll.cobol-executable-path");
  }

  @Test
  void shouldReportProcessCalculationMode() {
    final ProcessBackedCobolPayrollEngineClient processBackedCobolPayrollEngineClient =
        new ProcessBackedCobolPayrollEngineClient(shellExecutablePath());

    assertThat(processBackedCobolPayrollEngineClient.calculationEngineMode()).isEqualTo("process");
  }

  @Test
  void shouldThrowWhenTheConfiguredExecutableDoesNotExist() {
    final ProcessBackedCobolPayrollEngineClient processBackedCobolPayrollEngineClient =
        new ProcessBackedCobolPayrollEngineClient(Path.of("build", "missing-payroll-engine").toString());

    assertThatThrownBy(() -> processBackedCobolPayrollEngineClient.calculatePayroll(samplePayrollExecutionRequest("ignored")))
        .isInstanceOf(PayrollProcessingException.class)
        .hasMessageContaining("does not exist");
  }

  @Test
  void shouldCalculatePayrollFromShellDrivenOutput() {
    final ProcessBackedCobolPayrollEngineClient processBackedCobolPayrollEngineClient =
        new ProcessBackedCobolPayrollEngineClient(shellExecutablePath());

    final var payrollExecutionResponse = processBackedCobolPayrollEngineClient.calculatePayroll(
        samplePayrollExecutionRequest(shellCommandThatPrintsSuccessfulPayrollOutput()));

    assertThat(payrollExecutionResponse.employeeIdentifier()).isEqualTo("EMP-1001");
    assertThat(payrollExecutionResponse.grossPayAmount()).isEqualByComparingTo("3932.50");
    assertThat(payrollExecutionResponse.netPayAmount()).isEqualByComparingTo("2607.07");
  }

  @Test
  void shouldThrowWhenTheChildProcessReturnsANonZeroExitCode() {
    final ProcessBackedCobolPayrollEngineClient processBackedCobolPayrollEngineClient =
        new ProcessBackedCobolPayrollEngineClient(shellExecutablePath());

    assertThatThrownBy(() -> processBackedCobolPayrollEngineClient.calculatePayroll(
        samplePayrollExecutionRequest(shellCommandThatExitsWithCode(7))))
            .isInstanceOf(PayrollProcessingException.class)
            .hasMessageContaining("exited with code 7");
  }

  @Test
  void shouldThrowWhenTheChildProcessReportsAnApplicationError() {
    final ProcessBackedCobolPayrollEngineClient processBackedCobolPayrollEngineClient =
        new ProcessBackedCobolPayrollEngineClient(shellExecutablePath());

    assertThatThrownBy(() -> processBackedCobolPayrollEngineClient.calculatePayroll(
        samplePayrollExecutionRequest(shellCommandThatPrintsErrorMessage())))
            .isInstanceOf(PayrollProcessingException.class)
            .hasMessageContaining("reported an error");
  }

  @Test
  void shouldThrowWhenARequiredFieldIsMissingFromProcessOutput() {
    final ProcessBackedCobolPayrollEngineClient processBackedCobolPayrollEngineClient =
        new ProcessBackedCobolPayrollEngineClient(shellExecutablePath());

    assertThatThrownBy(() -> processBackedCobolPayrollEngineClient.calculatePayroll(
        samplePayrollExecutionRequest(shellCommandThatPrintsIncompleteOutput())))
            .isInstanceOf(PayrollProcessingException.class)
            .hasMessageContaining("did not return required field");
  }

  @Test
  void shouldWrapInputOutputFailuresWhenTheConfiguredFileIsNotExecutable() throws Exception {
    final Path plainTextFile = Files.createTempFile("not-an-executable", ".txt");
    Files.writeString(plainTextFile, "This is not an executable file.");

    try {
      final ProcessBackedCobolPayrollEngineClient processBackedCobolPayrollEngineClient =
          new ProcessBackedCobolPayrollEngineClient(plainTextFile.toString());

      assertThatThrownBy(() -> processBackedCobolPayrollEngineClient.calculatePayroll(
          samplePayrollExecutionRequest("ignored")))
              .isInstanceOf(PayrollProcessingException.class)
              .hasMessageContaining("Could not start or communicate");
    } finally {
      Files.deleteIfExists(plainTextFile);
    }
  }

  private String shellExecutablePath() {
    final String operatingSystemName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
    if (operatingSystemName.contains("win")) {
      return System.getenv().getOrDefault("ComSpec", "C:\\Windows\\System32\\cmd.exe");
    }
    return "/bin/sh";
  }

  private String shellCommandThatPrintsSuccessfulPayrollOutput() {
    if (isWindows()) {
      return String.join("& ",
          "echo ignored line",
          "echo employeeIdentifier=EMP-1001",
          "echo employeeFullName=Adriana Bennett",
          "echo grossRegularPayAmount=3400.00",
          "echo grossOvertimePayAmount=382.50",
          "echo grossPayAmount=3932.50",
          "echo taxWithholdingAmount=943.80",
          "echo retirementContributionAmount=196.63",
          "echo benefitDeductionAmount=185.00",
          "echo paidLeaveAccruedHours=3.31",
          "echo netPayAmount=2607.07",
          "exit /b 0");
    }

    return "printf '%s\\n' 'ignored line' 'employeeIdentifier=EMP-1001' 'employeeFullName=Adriana Bennett' "
        + "'grossRegularPayAmount=3400.00' 'grossOvertimePayAmount=382.50' 'grossPayAmount=3932.50' "
        + "'taxWithholdingAmount=943.80' 'retirementContributionAmount=196.63' "
        + "'benefitDeductionAmount=185.00' 'paidLeaveAccruedHours=3.31' 'netPayAmount=2607.07'; exit 0";
  }

  private String shellCommandThatExitsWithCode(final int processExitCode) {
    if (isWindows()) {
      return "exit /b " + processExitCode;
    }
    return "exit " + processExitCode;
  }

  private String shellCommandThatPrintsErrorMessage() {
    if (isWindows()) {
      return "echo errorMessage=The payroll engine could not compute the request.& exit /b 0";
    }
    return "printf '%s\\n' 'errorMessage=The payroll engine could not compute the request.'; exit 0";
  }

  private String shellCommandThatPrintsIncompleteOutput() {
    if (isWindows()) {
      return String.join("& ",
          "echo employeeIdentifier=EMP-1001",
          "echo employeeFullName=Adriana Bennett",
          "echo grossRegularPayAmount=3400.00",
          "echo grossOvertimePayAmount=382.50",
          "echo grossPayAmount=3932.50",
          "echo taxWithholdingAmount=943.80",
          "echo retirementContributionAmount=196.63",
          "echo benefitDeductionAmount=185.00",
          "echo paidLeaveAccruedHours=3.31",
          "echo netPayAmount=",
          "exit /b 0");
    }

    return "printf '%s\\n' 'employeeIdentifier=EMP-1001' 'employeeFullName=Adriana Bennett' "
        + "'grossRegularPayAmount=3400.00' 'grossOvertimePayAmount=382.50' 'grossPayAmount=3932.50' "
        + "'taxWithholdingAmount=943.80' 'retirementContributionAmount=196.63' "
        + "'benefitDeductionAmount=185.00' 'paidLeaveAccruedHours=3.31' 'netPayAmount='; exit 0";
  }

  private CobolPayrollExecutionRequest samplePayrollExecutionRequest(final String employeeIdentifier) {
    return new CobolPayrollExecutionRequest(
        employeeIdentifier,
        "unused full name",
        new BigDecimal("42.50"),
        new BigDecimal("80.00"),
        new BigDecimal("6.00"),
        new BigDecimal("0.24"),
        new BigDecimal("185.00"),
        new BigDecimal("150.00"));
  }

  private boolean isWindows() {
    return System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win");
  }
}
