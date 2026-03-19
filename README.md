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

## Start here

If you are completely new to the project, use these scripts in this exact
order:

1. Check what you need to install.
2. Build the project.
3. Run the application.
4. Run the tests.

Important:
`.ps1` files are PowerShell scripts, not Python scripts.
Use `powershell -ExecutionPolicy Bypass -File scripts/name.ps1` or run them
directly from PowerShell as `.\scripts\name.ps1`.

### Step 1: Check prerequisites

On Windows PowerShell:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/check-prerequisites.ps1
```

On Bash:

```bash
bash scripts/check-prerequisites.sh
```

### Step 2: Build the project

On Windows PowerShell:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/build-project.ps1
```

On Bash:

```bash
bash scripts/build-project.sh
```

### Step 3: Run the application in demonstration mode

On Windows PowerShell:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/run-application.ps1
```

On Bash:

```bash
bash scripts/run-application.sh
```

### Step 4: Run the tests

On Windows PowerShell:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/test-project.ps1
```

On Bash:

```bash
bash scripts/test-project.sh
```

## Additional manual commands

### Run the demonstration mode directly with Maven

```bash
mvn -pl spring-control-client spring-boot:run
```

### Run with the real COBOL executable

```bash
bash scripts/run-application.sh --use-cobol-process
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

The beginner-friendly full test entrypoint is:

```bash
bash scripts/test-project.sh
```
