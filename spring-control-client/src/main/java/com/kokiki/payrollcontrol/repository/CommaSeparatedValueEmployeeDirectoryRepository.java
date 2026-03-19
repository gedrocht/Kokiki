package com.kokiki.payrollcontrol.repository;

import com.kokiki.payrollcontrol.model.EmployeeRecord;
import com.kokiki.payrollcontrol.service.PayrollProcessingException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

/**
 * Loads employee records from a comma-separated value file on the classpath.
 *
 * <p>The class reads the file once during construction and then keeps the
 * records in memory. That keeps runtime behavior simple and predictable.</p>
 */
@Repository
public final class CommaSeparatedValueEmployeeDirectoryRepository implements EmployeeDirectoryRepository {

  private static final Logger APPLICATION_LOGGER =
      LoggerFactory.getLogger(CommaSeparatedValueEmployeeDirectoryRepository.class);
  private static final int MINIMUM_EMPLOYEE_DIRECTORY_LINE_COUNT = 2;
  private static final int EXPECTED_EMPLOYEE_DIRECTORY_COLUMN_COUNT = 8;

  private final List<EmployeeRecord> employeeRecords;

  /**
   * Creates the repository and loads employee data immediately so startup fails
   * early if the seed data file is missing or malformed.
   *
   * @param resourceLoader Spring resource loader used to read the classpath file
   */
  public CommaSeparatedValueEmployeeDirectoryRepository(final ResourceLoader resourceLoader) {
    this.employeeRecords = loadEmployeeRecords(resourceLoader);
  }

  @Override
  public List<EmployeeRecord> findAllEmployeeRecords() {
    return List.copyOf(employeeRecords);
  }

  @Override
  public Optional<EmployeeRecord> findEmployeeRecordByEmployeeIdentifier(final String employeeIdentifier) {
    return employeeRecords.stream()
        .filter(employeeRecord -> employeeRecord.employeeIdentifier().equals(employeeIdentifier))
        .findFirst();
  }

  /**
   * Reads and parses the classpath file.
   *
   * @param resourceLoader resource loader
   * @return immutable employee list
   */
  private List<EmployeeRecord> loadEmployeeRecords(final ResourceLoader resourceLoader) {
    final Resource employeeDirectoryResource =
        resourceLoader.getResource("classpath:fictional-company-employee-directory.csv");

    try {
      final List<String> employeeDirectoryLines = employeeDirectoryResource.getContentAsString(StandardCharsets.UTF_8)
          .lines()
          .toList();

      if (employeeDirectoryLines.size() < MINIMUM_EMPLOYEE_DIRECTORY_LINE_COUNT) {
        throw new PayrollProcessingException("The employee directory file did not contain employee data.");
      }

      final List<EmployeeRecord> loadedEmployeeRecords = employeeDirectoryLines.stream()
          .skip(1)
          .map(employeeDirectoryLine -> {
            final String[] commaSeparatedValues = employeeDirectoryLine.split(",", -1);

            if (commaSeparatedValues.length != 8) {
              throw new PayrollProcessingException(
                  "Expected 8 columns in the employee directory but found " + commaSeparatedValues.length + ".");
            }

            return new EmployeeRecord(
                commaSeparatedValues[0],
                commaSeparatedValues[1],
                commaSeparatedValues[2],
                commaSeparatedValues[3],
                new BigDecimal(commaSeparatedValues[4]),
                Boolean.parseBoolean(commaSeparatedValues[5]),
                new BigDecimal(commaSeparatedValues[6]),
                new BigDecimal(commaSeparatedValues[7]));
          })
          .toList();

      APPLICATION_LOGGER.info("Loaded {} employee records from the seed directory.", loadedEmployeeRecords.size());
      return List.copyOf(loadedEmployeeRecords);
    } catch (final IOException ioException) {
      throw new PayrollProcessingException("Could not read the employee directory file.", ioException);
    }
  }

  /**
   * Converts one comma-separated line into a strongly typed employee record.
   *
   * @param employeeDirectoryLine one line from the classpath file
   * @return parsed employee record
   */
  private EmployeeRecord convertLineIntoEmployeeRecord(final String employeeDirectoryLine) {
    final String[] commaSeparatedValues = employeeDirectoryLine.split(",", -1);

    if (commaSeparatedValues.length != EXPECTED_EMPLOYEE_DIRECTORY_COLUMN_COUNT) {
      throw new PayrollProcessingException(
          "Expected " + EXPECTED_EMPLOYEE_DIRECTORY_COLUMN_COUNT
              + " columns in the employee directory but found " + commaSeparatedValues.length + ".");
    }

    return new EmployeeRecord(
        commaSeparatedValues[0],
        commaSeparatedValues[1],
        commaSeparatedValues[2],
        commaSeparatedValues[3],
        new BigDecimal(commaSeparatedValues[4]),
        Boolean.parseBoolean(commaSeparatedValues[5]),
        new BigDecimal(commaSeparatedValues[6]),
        new BigDecimal(commaSeparatedValues[7]));
  }
}
