package com.kokiki.payrollcontrol.service;

import com.kokiki.payrollcontrol.model.CobolPayrollExecutionRequest;
import com.kokiki.payrollcontrol.model.CobolPayrollExecutionResponse;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Production-style payroll engine client that launches the COBOL executable as
 * a child process.
 */
@Service
@ConditionalOnProperty(name = "company-payroll.execution-mode", havingValue = "process")
public final class ProcessBackedCobolPayrollEngineClient implements PayrollEngineClient {

  private static final Logger APPLICATION_LOGGER = LoggerFactory.getLogger(ProcessBackedCobolPayrollEngineClient.class);
  private static final int OUTPUT_KEY_VALUE_PART_COUNT = 2;

  private final Path cobolExecutablePath;

  /**
   * Creates the client and validates that an executable path was configured.
   *
   * @param cobolExecutablePathText configured path from application properties
   */
  public ProcessBackedCobolPayrollEngineClient(
      @Value("${company-payroll.cobol-executable-path:}") final String cobolExecutablePathText) {
    if (cobolExecutablePathText == null || cobolExecutablePathText.isBlank()) {
      throw new PayrollProcessingException(
          "Process mode requires company-payroll.cobol-executable-path to point to the compiled COBOL executable.");
    }

    this.cobolExecutablePath = Path.of(cobolExecutablePathText).normalize();
  }

  @Override
  public CobolPayrollExecutionResponse calculatePayroll(final CobolPayrollExecutionRequest payrollExecutionRequest) {
    if (!Files.exists(cobolExecutablePath)) {
      throw new PayrollProcessingException(
          "The configured COBOL executable does not exist: " + cobolExecutablePath);
    }

    APPLICATION_LOGGER.info("Launching COBOL payroll engine at {}.", cobolExecutablePath);

    try {
      final Process payrollProcess = new ProcessBuilder(cobolExecutablePath.toString())
          .redirectErrorStream(true)
          .start();

      writePayrollRequestToProcess(payrollProcess, payrollExecutionRequest);
      final Map<String, String> processOutputValues = readProcessOutputValues(payrollProcess);
      final int processExitCode = payrollProcess.waitFor();

      if (processExitCode != 0) {
        throw new PayrollProcessingException(
            "The COBOL payroll engine exited with code " + processExitCode + " and output " + processOutputValues + ".");
      }

      if (processOutputValues.containsKey("errorMessage")) {
        throw new PayrollProcessingException(
            "The COBOL payroll engine reported an error: " + processOutputValues.get("errorMessage"));
      }

      return new CobolPayrollExecutionResponse(
          requiredValue(processOutputValues, "employeeIdentifier"),
          requiredValue(processOutputValues, "employeeFullName"),
          new BigDecimal(requiredValue(processOutputValues, "grossRegularPayAmount")),
          new BigDecimal(requiredValue(processOutputValues, "grossOvertimePayAmount")),
          new BigDecimal(requiredValue(processOutputValues, "grossPayAmount")),
          new BigDecimal(requiredValue(processOutputValues, "taxWithholdingAmount")),
          new BigDecimal(requiredValue(processOutputValues, "retirementContributionAmount")),
          new BigDecimal(requiredValue(processOutputValues, "benefitDeductionAmount")),
          new BigDecimal(requiredValue(processOutputValues, "paidLeaveAccruedHours")),
          new BigDecimal(requiredValue(processOutputValues, "netPayAmount")));
    } catch (final IOException ioException) {
      throw new PayrollProcessingException("Could not start or communicate with the COBOL payroll engine.", ioException);
    } catch (final InterruptedException interruptedException) {
      Thread.currentThread().interrupt();
      throw new PayrollProcessingException("The payroll engine process was interrupted.", interruptedException);
    }
  }

  @Override
  public String calculationEngineMode() {
    return "process";
  }

  /**
   * Writes one value per line to standard input. This mirrors the reading order
   * in the COBOL program so the contract stays easy to reason about.
   *
   * @param payrollProcess child process
   * @param payrollExecutionRequest payroll input
   * @throws IOException when writing fails
   */
  private void writePayrollRequestToProcess(
      final Process payrollProcess,
      final CobolPayrollExecutionRequest payrollExecutionRequest) throws IOException {
    try (BufferedWriter processInputWriter =
        new BufferedWriter(new OutputStreamWriter(payrollProcess.getOutputStream(), StandardCharsets.UTF_8))) {
      processInputWriter.write(payrollExecutionRequest.employeeIdentifier());
      processInputWriter.newLine();
      processInputWriter.write(payrollExecutionRequest.employeeFullName());
      processInputWriter.newLine();
      processInputWriter.write(payrollExecutionRequest.hourlyWageAmount().toPlainString());
      processInputWriter.newLine();
      processInputWriter.write(payrollExecutionRequest.regularHoursWorked().toPlainString());
      processInputWriter.newLine();
      processInputWriter.write(payrollExecutionRequest.overtimeHoursWorked().toPlainString());
      processInputWriter.newLine();
      processInputWriter.write(payrollExecutionRequest.standardTaxRatePercentage().toPlainString());
      processInputWriter.newLine();
      processInputWriter.write(payrollExecutionRequest.benefitDeductionAmount().toPlainString());
      processInputWriter.newLine();
      processInputWriter.write(payrollExecutionRequest.performanceBonusAmount().toPlainString());
      processInputWriter.newLine();
      processInputWriter.flush();
    }
  }

  /**
   * Reads key-value output lines emitted by the COBOL process.
   *
   * @param payrollProcess child process
   * @return ordered map of output values
   * @throws IOException when reading fails
   */
  private Map<String, String> readProcessOutputValues(final Process payrollProcess) throws IOException {
    final Map<String, String> processOutputValues = new LinkedHashMap<>();

    try (BufferedReader processOutputReader =
        new BufferedReader(new InputStreamReader(payrollProcess.getInputStream(), StandardCharsets.UTF_8))) {
      String outputLine = processOutputReader.readLine();
      while (outputLine != null) {
        final String[] outputLineParts = outputLine.split("=", 2);
        if (outputLineParts.length == OUTPUT_KEY_VALUE_PART_COUNT) {
          processOutputValues.put(outputLineParts[0].trim(), outputLineParts[1].trim());
        }
        outputLine = processOutputReader.readLine();
      }
    }

    return processOutputValues;
  }

  /**
   * Retrieves a required key from the process output map.
   *
   * @param processOutputValues parsed output map
   * @param requiredKey key that must exist
   * @return value stored under the key
   */
  private String requiredValue(final Map<String, String> processOutputValues, final String requiredKey) {
    final String locatedValue = processOutputValues.get(requiredKey);
    if (locatedValue == null || locatedValue.isBlank()) {
      throw new PayrollProcessingException("The COBOL payroll engine did not return required field " + requiredKey + ".");
    }
    return locatedValue;
  }
}
