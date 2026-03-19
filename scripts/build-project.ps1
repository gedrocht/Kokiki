[CmdletBinding()]
param()

$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

# This script gives the repository a single obvious build entrypoint from the
# top-level scripts folder. It compiles the COBOL engine when GNU COBOL is
# available and then runs the Maven verification pipeline for the Java module.
$repositoryRootDirectory = Split-Path -Parent $PSScriptRoot
$cobolCompilerCommand = Get-Command cobc -ErrorAction SilentlyContinue
$mavenCommand = Get-Command mvn -ErrorAction SilentlyContinue

if (-not $mavenCommand) {
  throw "Apache Maven is required to build this repository. Install Maven and try again."
}

Push-Location $repositoryRootDirectory
try {
  $mavenArguments = @("-B", "-ntp", "clean", "verify")

  if ($cobolCompilerCommand) {
    Write-Host "GNU COBOL detected. Compiling the COBOL payroll engine first."
    & (Join-Path $repositoryRootDirectory "cobol-core/scripts/compile-payroll-engine.ps1")

    $compiledCobolExecutablePath = Join-Path $repositoryRootDirectory "cobol-core/build/payroll-calculation-engine.exe"
    $mavenArguments += "-Dcobol.payroll.executable.path=$compiledCobolExecutablePath"
  } else {
    Write-Warning "GNU COBOL was not found. The Java build will continue in demonstration mode without the real COBOL integration test."
  }

  Write-Host "Running Maven verification."
  & mvn @mavenArguments
} finally {
  Pop-Location
}
