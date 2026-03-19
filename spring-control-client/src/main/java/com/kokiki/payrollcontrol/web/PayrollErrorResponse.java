package com.kokiki.payrollcontrol.web;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Standard error body returned by the REST layer.
 *
 * @param timestamp moment when the error response was created
 * @param errorCategory short machine-readable error category
 * @param message human-readable explanation
 * @param details validation or debugging details
 */
public record PayrollErrorResponse(
    OffsetDateTime timestamp,
    String errorCategory,
    String message,
    List<String> details) {

  /**
   * Creates an immutable error response snapshot.
   *
   * <p>The defensive copy keeps callers from changing the stored error details
   * after the response is created.</p>
   */
  public PayrollErrorResponse {
    details = List.copyOf(details);
  }

  @Override
  public List<String> details() {
    return List.copyOf(details);
  }
}
