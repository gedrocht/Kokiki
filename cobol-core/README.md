# COBOL Core

This folder contains the payroll calculation engine for the fictional Blue Sky
Office Design company.

## Responsibilities

- Receive normalized payroll input from standard input
- Calculate regular pay, overtime pay, taxes, retirement contribution, benefit
  deductions, and paid leave accrual
- Return key-value output that the Spring Boot control client can parse safely

## Build

```bash
bash scripts/compile-payroll-engine.sh
```

## Example input order

1. Employee identifier
2. Employee full name
3. Hourly wage amount
4. Regular hours worked
5. Overtime hours worked
6. Standard tax rate percentage
7. Benefit deduction amount
8. Performance bonus amount
