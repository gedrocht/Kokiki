package com.kokiki.payrollcontrol.web;

import com.kokiki.payrollcontrol.service.EmployeeRecordNotFoundException;
import com.kokiki.payrollcontrol.service.PayrollProcessingException;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Converts internal exceptions into consistent HTTP responses.
 */
@RestControllerAdvice
public final class RestExceptionHandler {

  /**
   * Handles employee-not-found failures.
   *
   * @param employeeRecordNotFoundException thrown exception
   * @return HTTP 404 response
   */
  @ExceptionHandler(EmployeeRecordNotFoundException.class)
  public ResponseEntity<PayrollErrorResponse> handleEmployeeRecordNotFound(
      final EmployeeRecordNotFoundException employeeRecordNotFoundException) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new PayrollErrorResponse(
            OffsetDateTime.now(),
            "EMPLOYEE_NOT_FOUND",
            employeeRecordNotFoundException.getMessage(),
            List.of()));
  }

  /**
   * Handles payroll engine failures.
   *
   * @param payrollProcessingException thrown exception
   * @return HTTP 500 response
   */
  @ExceptionHandler(PayrollProcessingException.class)
  public ResponseEntity<PayrollErrorResponse> handlePayrollProcessingFailure(
      final PayrollProcessingException payrollProcessingException) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new PayrollErrorResponse(
            OffsetDateTime.now(),
            "PAYROLL_PROCESSING_FAILURE",
            payrollProcessingException.getMessage(),
            List.of()));
  }

  /**
   * Handles validation failures from request bodies.
   *
   * @param methodArgumentNotValidException thrown exception
   * @return HTTP 400 response
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<PayrollErrorResponse> handleValidationFailure(
      final MethodArgumentNotValidException methodArgumentNotValidException) {
    final List<String> validationMessages = methodArgumentNotValidException.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(FieldError::getDefaultMessage)
        .toList();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new PayrollErrorResponse(
            OffsetDateTime.now(),
            "VALIDATION_FAILURE",
            "The payroll request body was invalid.",
            validationMessages));
  }
}
