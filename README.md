# Kokiki Payroll Platform

Kokiki is a fictional payroll modernization repository for **Blue Sky Office
Design**, a 30-person company that wants to keep dependable COBOL payroll logic
while operating it through a modern Java Spring Boot control client.

## What the project does

- Calculates payroll for a 30-employee company
- Handles overtime, bonus amounts, benefit deductions, retirement contribution,
  tax withholding, and paid leave accrual
- Exposes a REST interface for payroll specialists and modern tools
- Produces structured JSON logs and Prometheus metrics
- Ships with beginner-first docs, generated source reference, and a separately
  deployable Wiki.js knowledge base
- Enforces strict GitHub quality, coverage, security, and review gates

## Repository layout

- `cobol-core/`: the COBOL payroll engine and compilation helpers
- `spring-control-client/`: the Spring Boot REST client and orchestration layer
- `docs/`: the MkDocs documentation site
- `wiki/`: Wiki.js deployment and starter pages
- `infra/observability/`: Grafana, Loki, Promtail, and Prometheus starter stack
- `config/`: Checkstyle, PMD, and SpotBugs configuration
- `.github/workflows/`: CI, security, docs, and scorecard automation

## Quick start

### Build the project from the top-level scripts folder

On Windows PowerShell:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/build-project.ps1
```

On Bash:

```bash
./scripts/build-project.sh
```

### Run the demonstration mode

```bash
mvn -pl spring-control-client spring-boot:run
```

### Run with the real COBOL executable

```bash
./cobol-core/scripts/compile-payroll-engine.sh
mvn -pl spring-control-client spring-boot:run \
  -Dspring-boot.run.arguments=--company-payroll.execution-mode=process,--company-payroll.cobol-executable-path=../cobol-core/build/payroll-calculation-engine
```

### Example payroll request

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

## Quality and security standards

This repository intentionally aims high. The automated checks include:

- Java unit, web, architecture, and integration tests
- JaCoCo coverage thresholds
- Checkstyle, PMD, and SpotBugs
- Action linting, Markdown linting, YAML linting, and PowerShell analysis
- Repository policy tests
- Dependency review on pull requests
- Gitleaks secret scanning
- Trivy filesystem scanning
- CodeQL analysis
- OSSF Scorecard checks
- GitHub Pages documentation builds

GitHub-native dependency review also requires the repository-level **Dependency
Graph** setting to be enabled in GitHub.

## Documentation

- Narrative docs site: `docs/` via MkDocs Material
- Generated source reference: Doxygen output
- Separate serveable wiki: Wiki.js in `wiki/`
- Library references with official links:
  [docs/reference/external-libraries.md](docs/reference/external-libraries.md)

## Local validation

```powershell
powershell -ExecutionPolicy Bypass -File scripts/validate-repo.ps1
```

If Java and Maven are installed:

```bash
mvn clean verify
```

If you want the build script to handle that for you:

```bash
./scripts/build-project.sh
```
