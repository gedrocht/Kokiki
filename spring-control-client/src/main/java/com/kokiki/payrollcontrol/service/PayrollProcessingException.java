package com.kokiki.payrollcontrol.service;

/**
 * Raised when the payroll process cannot be completed safely.
 */
public final class PayrollProcessingException extends RuntimeException {

  /**
   * Creates an exception with a message.
   *
   * @param message explanation of the failure
   */
  public PayrollProcessingException(final String message) {
    super(message);
  }

  /**
   * Creates an exception with both a message and a cause.
   *
   * @param message explanation of the failure
   * @param cause original exception
   */
  public PayrollProcessingException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
