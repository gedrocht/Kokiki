[CmdletBinding()]
param()

$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

$repoRoot = Split-Path -Parent $PSScriptRoot

$requiredFiles = @(
  ".editorconfig",
  ".gitattributes",
  ".markdownlint-cli2.jsonc",
  ".github/dependabot.yml",
  ".github/ISSUE_TEMPLATE/bug-report.yml",
  ".github/ISSUE_TEMPLATE/improvement-request.yml",
  ".github/pull_request_template.md",
  ".github/workflows/documentation.yml",
  ".github/workflows/quality.yml",
  ".github/workflows/security.yml",
  ".github/workflows/scorecard.yml",
  ".yamllint.yml",
  "Doxyfile",
  "CODEOWNERS",
  "CONTRIBUTING.md",
  "LICENSE",
  "NOTICE",
  "README.md",
  "SECURITY.md",
  "cobol-core/src/main/cobol/payroll_calculation_engine.cbl",
  "scripts/build-project.ps1",
  "scripts/build-project.sh",
  "spring-control-client/pom.xml",
  "spring-control-client/src/main/resources/fictional-company-employee-directory.csv",
  "wiki/docker-compose.yml",
  "mkdocs.yml"
)

$missing = foreach ($relativePath in $requiredFiles) {
  $path = Join-Path $repoRoot $relativePath
  if (-not (Test-Path -LiteralPath $path)) {
    $relativePath
  }
}

if ($missing) {
  throw "Missing required repository files:`n - $($missing -join "`n - ")"
}

$workflowFiles = Get-ChildItem -Path (Join-Path $repoRoot ".github/workflows") -File -Filter "*.yml"
if (-not $workflowFiles) {
  throw "No GitHub Actions workflows were found."
}

$requiredTriggers = @("pull_request", "push")
foreach ($workflow in $workflowFiles) {
  $content = Get-Content -LiteralPath $workflow.FullName -Raw
  foreach ($trigger in $requiredTriggers) {
    if ($workflow.Name -eq "scorecard.yml") {
      continue
    }

    if ($content -notmatch "(?m)^\s*$trigger\s*:") {
      throw "Workflow '$($workflow.Name)' must define '$trigger'."
    }
  }
}

$contentChecks = @{
  ".github/workflows/quality.yml" = @(
    "actionlint",
    "pymarkdown",
    "yamllint",
    "PSScriptAnalyzer",
    "mvn -B -ntp -pl spring-control-client -am verify",
    "gnucobol"
  )
  ".github/workflows/security.yml" = @(
    "gitleaks",
    "dependency-review-action",
    "trivy",
    "github/codeql-action/init"
  )
  ".github/workflows/documentation.yml" = @(
    "mkdocs build",
    "doxygen",
    "upload-pages-artifact"
  )
  ".github/workflows/scorecard.yml" = @(
    "scorecard-action"
  )
}

foreach ($relativePath in $contentChecks.Keys) {
  $path = Join-Path $repoRoot $relativePath
  $content = Get-Content -LiteralPath $path -Raw
  foreach ($needle in $contentChecks[$relativePath]) {
    if ($content -notmatch [Regex]::Escape($needle)) {
      throw "Expected '$needle' in '$relativePath'."
    }
  }
}

$employeeDirectoryPath = Join-Path $repoRoot "spring-control-client/src/main/resources/fictional-company-employee-directory.csv"
$employeeDirectoryLineCount = (Get-Content -LiteralPath $employeeDirectoryPath | Measure-Object -Line).Lines
if ($employeeDirectoryLineCount -ne 31) {
  throw "Expected 31 lines in the employee directory file, including the header, but found $employeeDirectoryLineCount."
}

$readmeContent = Get-Content -LiteralPath (Join-Path $repoRoot "README.md") -Raw
if ($readmeContent -notmatch "COBOL" -or $readmeContent -notmatch "Spring Boot" -or $readmeContent -notmatch "Wiki\.js") {
  throw "README.md must describe the COBOL engine, Spring Boot client, and Wiki.js documentation layer."
}

Write-Host "Repository policy validation passed."
