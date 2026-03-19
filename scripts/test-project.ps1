[CmdletBinding()]
param()

$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

# This script gives beginners one obvious test command. It runs the repository
# policy validation first and then runs the Maven verification pipeline. If GNU
# COBOL is installed, it also passes the compiled COBOL executable path so the
# real COBOL integration test can participate.
$repositoryRootDirectory = Split-Path -Parent $PSScriptRoot

Write-Output "Running repository policy validation."
& (Join-Path $repositoryRootDirectory "scripts/validate-repo.ps1")

Write-Output "Running full project build and test verification."
& (Join-Path $repositoryRootDirectory "scripts/build-project.ps1")
