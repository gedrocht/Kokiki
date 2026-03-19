# How The COBOL Engine Receives Input

The Spring Boot process launches the COBOL executable and writes one value per
line into the COBOL process standard input stream.

## Input order

1. Employee identifier
2. Employee full name
3. Hourly wage amount
4. Regular hours worked
5. Overtime hours worked
6. Standard tax rate percentage
7. Benefit deduction amount
8. Performance bonus amount

The COBOL program reads these values using `ACCEPT` statements in the exact same
order.
