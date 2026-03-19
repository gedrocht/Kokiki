package com.kokiki.payrollcontrol.repository;

import com.kokiki.payrollcontrol.model.EmployeeRecord;
import java.util.List;
import java.util.Optional;

/**
 * Abstraction around employee directory storage.
 *
 * <p>In a larger production system this could read from a relational database,
 * an enterprise service bus, or a human resources platform. In this repository
 * we intentionally use a small comma-separated value file so beginners can see
 * the source of truth immediately.</p>
 */
public interface EmployeeDirectoryRepository {

  /**
   * Returns every employee record known to the application.
   *
   * @return immutable employee list
   */
  List<EmployeeRecord> findAllEmployeeRecords();

  /**
   * Finds one employee by identifier.
   *
   * @param employeeIdentifier employee identifier to search for
   * @return employee record when present
   */
  Optional<EmployeeRecord> findEmployeeRecordByEmployeeIdentifier(String employeeIdentifier);
}
