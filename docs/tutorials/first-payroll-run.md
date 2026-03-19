# First Payroll Run

This walkthrough shows the entire control flow from HTTP request to COBOL
calculation.

## Step 1: Build the COBOL executable

```bash
./cobol-core/scripts/compile-payroll-engine.sh
```

## Step 2: Start the Spring Boot control client

```bash
mvn -pl spring-control-client spring-boot:run \
  -Dspring-boot.run.arguments=--company-payroll.execution-mode=process,--company-payroll.cobol-executable-path=../cobol-core/build/payroll-calculation-engine
```

## Step 3: Submit a sample payroll request

```bash
curl --request POST http://localhost:8080/application-programming-interface/payroll/calculations \
  --header "Content-Type: application/json" \
  --data '{
    "employeeIdentifier":"EMP-1001",
    "regularHoursWorked":80.0,
    "overtimeHoursWorked":6.0,
    "performanceBonusAmount":150.00
  }'
```

## What you should observe

- The Spring Boot application will log the request, the orchestration step, and
  the engine mode that was used.
- The COBOL engine will calculate regular pay, overtime pay, tax withholding,
  retirement contribution, benefit deduction, net pay, and paid leave accrual.
- The response body can be inspected in the REST examples page.
