BeforeAll {
  $repoRoot = Split-Path -Parent $PSScriptRoot
}

Describe "Repository baseline" {
  It "includes the required governance files" {
    @(
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
    ) | ForEach-Object {
      $path = Join-Path $repoRoot $_
      $path | Should -Exist
    }
  }

  It "includes workflow hardening on every workflow" {
    $workflowFiles = Get-ChildItem -Path (Join-Path $repoRoot ".github/workflows") -File -Filter "*.yml"
    $workflowFiles.Count | Should -BeGreaterThan 0

    foreach ($workflow in $workflowFiles) {
      $content = Get-Content -LiteralPath $workflow.FullName -Raw
      $content | Should -Match "step-security/harden-runner"
    }
  }

  It "keeps security scanning enabled" {
    $securityWorkflow = Get-Content -LiteralPath (Join-Path $repoRoot ".github/workflows/security.yml") -Raw
    $securityWorkflow | Should -Match "gitleaks"
    $securityWorkflow | Should -Match "dependency-review-action"
    $securityWorkflow | Should -Match "trivy"
    $securityWorkflow | Should -Match "codeql"
    $securityWorkflow | Should -Match "Dependency Graph is disabled"
  }

  It "keeps beginner-facing documentation layers in place" {
    $readmeContent = Get-Content -LiteralPath (Join-Path $repoRoot "README.md") -Raw
    $readmeContent | Should -Match "COBOL"
    $readmeContent | Should -Match "Spring Boot"
    $readmeContent | Should -Match "Wiki.js"
  }

  It "ships a thirty-employee directory plus the header row" {
    $employeeDirectoryPath = Join-Path $repoRoot "spring-control-client/src/main/resources/fictional-company-employee-directory.csv"
    ((Get-Content -LiteralPath $employeeDirectoryPath).Count) | Should -Be 31
  }
}
