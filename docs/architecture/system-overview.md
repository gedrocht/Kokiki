# System Overview

The repository is organized around one simple idea: keep payroll rules in a
deterministic COBOL core and let a Spring Boot service handle access, logging,
testing, validation, and documentation.

## Main layers

- `cobol-core/`: the payroll calculation engine and compilation scripts.
- `spring-control-client/`: the REST application, domain orchestration, and
  structured logging.
- `docs/`: the beginner-friendly documentation site built with MkDocs.
- `wiki/`: Wiki.js deployment files for a separately hosted wiki experience.
- `infra/observability/`: Grafana, Loki, Promtail, and Prometheus starter
  configuration.

## Control flow

1. A payroll specialist sends a request to the Spring Boot application.
2. The application validates the request and loads the employee record.
3. The COBOL engine receives normalized payroll input over standard input.
4. The COBOL engine prints key-value results.
5. The Java adapter parses the output and returns a structured REST response.
