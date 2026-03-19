# Contributing

This repository is intentionally strict because it is designed as a teaching
artifact and as a demonstration of a high-assurance GitHub setup.

## Before you open a pull request

1. Read the beginner documentation and repository tour.
2. Keep naming descriptive and avoid abbreviations in new variables.
3. Add explanatory comments where a beginner would struggle without them.
4. Update tutorials and reference documentation when behavior changes.
5. Keep tests, coverage, and quality checks passing.

## Expected quality bar

- New code should include unit tests.
- New HTTP behavior should include web-layer tests.
- Cross-layer rules should stay covered by architecture tests.
- Coverage must stay above the configured JaCoCo thresholds.
- Pull requests should not introduce new high-severity vulnerabilities.

## Review expectations

- At least one CODEOWNERS review is expected.
- Security-sensitive changes should explain their threat model impact.
- Documentation changes are welcome and encouraged, especially if they make the
  code easier for beginners to understand.
