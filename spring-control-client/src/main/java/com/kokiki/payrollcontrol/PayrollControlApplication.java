package com.kokiki.payrollcontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Spring Boot control client.
 *
 * <p>This class has one responsibility: start the modern Java layer that sits
 * in front of the COBOL payroll calculation engine. A beginner can think of
 * this class as the "front door" of the application.</p>
 */
@SpringBootApplication
public class PayrollControlApplication {

  /**
   * Starts the Spring Boot application.
   *
   * @param commandLineArguments values passed from the operating system when
   *     the application starts
   */
  public static void main(final String[] commandLineArguments) {
    SpringApplication.run(PayrollControlApplication.class, commandLineArguments);
  }
}
