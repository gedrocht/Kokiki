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
  ".github/pull_request_template.md",
  ".github/workflows/quality.yml",
  ".github/workflows/security.yml",
  ".github/workflows/scorecard.yml",
  ".yamllint.yml",
  "CODEOWNERS",
  "CONTRIBUTING.md",
  "README.md",
  "SECURITY.md"
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
    "markdownlint",
    "yamllint",
    "PSScriptAnalyzer"
  )
  ".github/workflows/security.yml" = @(
    "gitleaks",
    "dependency-review-action",
    "trivy"
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

Write-Host "Repository policy validation passed."
