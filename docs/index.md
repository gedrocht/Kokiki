# Kokiki Payroll Platform

The Kokiki Payroll Platform is a fictional modernization project for **Blue Sky
Office Design**, a 30-person commercial interior design company that wants to
keep a trustworthy COBOL payroll engine while giving managers and payroll staff
a modern Spring Boot control surface.

This documentation is intentionally written for complete beginners first. If
you have never seen COBOL, Spring Boot, GitHub Actions, or observability tools
before, start with the tutorial pages and then move into the architecture and
reference pages.

## What this repository contains

- A COBOL payroll engine that calculates pay, overtime, taxes, retirement
  withholding, benefit deductions, and paid leave accrual.
- A Java Spring Boot application that exposes REST endpoints and can call the
  COBOL executable as a subprocess.
- Thorough narrative documentation, generated source reference, and a separate
  wiki deployment based on Wiki.js.
- GitHub workflows that enforce strict quality, coverage, security, dependency,
  and documentation standards.

## Recommended reading order

1. [Prerequisites And First Run](tutorials/prerequisites-and-first-run.md)
2. [Getting Started](tutorials/getting-started.md)
3. [First Payroll Run](tutorials/first-payroll-run.md)
4. [Reading The COBOL Source](tutorials/reading-the-cobol-source.md)
5. [System Overview](architecture/system-overview.md)
6. [REST Endpoints](reference/rest-endpoints.md)
