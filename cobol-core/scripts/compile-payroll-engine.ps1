[CmdletBinding()]
param()

$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

# This script mirrors the Bash compiler helper so Windows users can compile the
# payroll engine without leaving PowerShell.
$repositoryRootDirectory = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
$buildOutputDirectory = Join-Path $repositoryRootDirectory "cobol-core/build"
$sourceProgramFilePath = Join-Path $repositoryRootDirectory "cobol-core/src/main/cobol/payroll_calculation_engine.cbl"
$compiledExecutableFilePath = Join-Path $buildOutputDirectory "payroll-calculation-engine.exe"

New-Item -ItemType Directory -Path $buildOutputDirectory -Force | Out-Null
& cobc -x -free -o $compiledExecutableFilePath $sourceProgramFilePath

Write-Output "Compiled COBOL payroll engine to $compiledExecutableFilePath"
