# REST Endpoints

## List employees

`GET /application-programming-interface/employees`

Example response:

```json
[
  {
    "employeeIdentifier": "EMP-1001",
    "employeeFullName": "Adriana Bennett",
    "departmentName": "Payroll",
    "roleTitle": "Payroll Manager",
    "hourlyWageAmount": 42.50,
    "overtimeEligible": true,
    "standardTaxRatePercentage": 0.24,
    "benefitDeductionAmount": 185.00
  }
]
```

## Calculate payroll

`POST /application-programming-interface/payroll/calculations`

Example request:

```json
{
  "employeeIdentifier": "EMP-1001",
  "regularHoursWorked": 80.0,
  "overtimeHoursWorked": 6.0,
  "performanceBonusAmount": 150.00
}
```

Example response:

```json
{
  "employeeIdentifier": "EMP-1001",
  "employeeFullName": "Adriana Bennett",
  "grossRegularPayAmount": 3400.00,
  "grossOvertimePayAmount": 382.50,
  "grossPayAmount": 3932.50,
  "taxWithholdingAmount": 943.80,
  "retirementContributionAmount": 196.63,
  "benefitDeductionAmount": 185.00,
  "paidLeaveAccruedHours": 3.31,
  "netPayAmount": 2607.07,
  "calculationEngineMode": "process"
}
```
