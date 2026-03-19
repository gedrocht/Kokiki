package com.kokiki.payrollcontrol.service;

/**
 * Raised when a requested employee identifier does not exist in the directory.
 */
public final class EmployeeRecordNotFoundException extends RuntimeException {

  /**
   * Creates an exception with a message.
   *
   * @param message explanation of the failure
   */
  public EmployeeRecordNotFoundException(final String message) {
    super(message);
  }
}
