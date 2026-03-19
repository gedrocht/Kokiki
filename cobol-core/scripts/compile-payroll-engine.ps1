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
$gnuCobolEnvironmentSetupScriptPath = "C:\ProgramData\chocolatey\lib\gnucobol\tools\set_env.cmd"

New-Item -ItemType Directory -Path $buildOutputDirectory -Force | Out-Null

if (Test-Path -LiteralPath $gnuCobolEnvironmentSetupScriptPath) {
  $windowsCobolCompileCommand = @(
    "/c"
    "call `"$gnuCobolEnvironmentSetupScriptPath`" && cobc -x -free -o `"$compiledExecutableFilePath`" `"$sourceProgramFilePath`""
  )
  & cmd.exe @windowsCobolCompileCommand
} else {
  & cobc -x -free -o $compiledExecutableFilePath $sourceProgramFilePath
}

if ($LASTEXITCODE -ne 0 -or -not (Test-Path -LiteralPath $compiledExecutableFilePath)) {
  throw "GNU COBOL compilation failed."
}

Write-Output "Compiled COBOL payroll engine to $compiledExecutableFilePath"
