# Getting Started

This repository demonstrates how a small company can preserve dependable COBOL
business logic while adding a modern operational and user-facing layer around
it.

## Fictional company background

Blue Sky Office Design has 30 employees across payroll, project management,
sales, design, warehousing, installation, and leadership. The company pays most
staff hourly, so overtime, tax withholding, benefit deductions, and clean audit
trails matter.

## Beginner checklist

1. Run `scripts/check-prerequisites.ps1` or `scripts/check-prerequisites.sh`.
2. Read the [README](../README.md) to understand the repository layout.
3. Study the employee directory file in
   `spring-control-client/src/main/resources/fictional-company-employee-directory.csv`.
4. Run `scripts/build-project.ps1` or `scripts/build-project.sh` from the
   repository root.
5. Run `scripts/run-application.ps1` or `scripts/run-application.sh`.
6. Run `scripts/test-project.ps1` or `scripts/test-project.sh`.
7. Read the COBOL engine source before reading the Java adapter.
8. Review the GitHub workflows to understand the quality gates.

## Why use COBOL here?

COBOL is still strong at precise business data processing, fixed rules, and
highly readable transaction-oriented logic. This repository uses COBOL as the
deterministic payroll calculation engine and uses Java for integration,
presentation, testing, and observability.
