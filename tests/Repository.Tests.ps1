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
      ".github/pull_request_template.md",
      ".github/workflows/quality.yml",
      ".github/workflows/security.yml",
      ".github/workflows/scorecard.yml",
      ".yamllint.yml",
      "CODEOWNERS",
      "CONTRIBUTING.md",
      "README.md",
      "SECURITY.md"
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
  }
}
