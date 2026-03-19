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

Write-Output ""
Write-Output "Kokiki prerequisite check"
Write-Output "========================="
Write-Output ""

foreach ($requiredTool in $requiredTools) {
  $detectedCommand = Get-Command $requiredTool.CommandName -ErrorAction SilentlyContinue

  if ($detectedCommand) {
    Write-Output "[FOUND] $($requiredTool.ToolName)"
  } else {
    Write-Output "[MISSING] $($requiredTool.ToolName)"
    Write-Output "          Download: $($requiredTool.DownloadUrl)"
  }
}

Write-Output ""
Write-Output "Recommended beginner workflow"
Write-Output "-----------------------------"
Write-Output "PowerShell note: .ps1 files are PowerShell scripts."
Write-Output "Run them with powershell -ExecutionPolicy Bypass -File ... or from PowerShell as .\scripts\name.ps1"
Write-Output ""
Write-Output "1. Build the project:"
Write-Output "   powershell -ExecutionPolicy Bypass -File scripts/build-project.ps1"
Write-Output "2. Run the application:"
Write-Output "   powershell -ExecutionPolicy Bypass -File scripts/run-application.ps1"
Write-Output "3. Run the tests:"
Write-Output "   powershell -ExecutionPolicy Bypass -File scripts/test-project.ps1"
