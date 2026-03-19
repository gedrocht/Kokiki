[CmdletBinding()]
param()

$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

# This script is intentionally beginner-focused. It checks for the tools that
# the repository needs and prints the exact next steps that a new contributor
# should take.
$requiredTools = @(
  @{
    ToolName = "Git"
    CommandName = "git"
    DownloadUrl = "https://git-scm.com/downloads"
  },
  @{
    ToolName = "Java 21 or newer"
    CommandName = "java"
    DownloadUrl = "https://adoptium.net/"
  },
  @{
    ToolName = "Apache Maven"
    CommandName = "mvn"
    DownloadUrl = "https://maven.apache.org/download.cgi"
  },
  @{
    ToolName = "GNU COBOL (optional but recommended for the real COBOL integration path)"
    CommandName = "cobc"
    DownloadUrl = "https://gnucobol.sourceforge.io/"
  }
)

Write-Host ""
Write-Host "Kokiki prerequisite check"
Write-Host "========================="
Write-Host ""

foreach ($requiredTool in $requiredTools) {
  $detectedCommand = Get-Command $requiredTool.CommandName -ErrorAction SilentlyContinue

  if ($detectedCommand) {
    Write-Host "[FOUND] $($requiredTool.ToolName)"
  } else {
    Write-Host "[MISSING] $($requiredTool.ToolName)"
    Write-Host "          Download: $($requiredTool.DownloadUrl)"
  }
}

Write-Host ""
Write-Host "Recommended beginner workflow"
Write-Host "-----------------------------"
Write-Host "1. Build the project:"
Write-Host "   powershell -ExecutionPolicy Bypass -File scripts/build-project.ps1"
Write-Host "2. Run the application:"
Write-Host "   powershell -ExecutionPolicy Bypass -File scripts/run-application.ps1"
Write-Host "3. Run the tests:"
Write-Host "   powershell -ExecutionPolicy Bypass -File scripts/test-project.ps1"
