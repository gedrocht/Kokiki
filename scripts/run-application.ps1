[CmdletBinding()]
param(
  [switch]$UseCobolProcess
)

$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

# This script gives beginners one obvious way to start the application. By
# default it runs the Spring Boot client in demonstration mode. When the caller
# asks for the real COBOL path and GNU COBOL is installed, the script compiles
# the COBOL engine first and then starts Spring Boot with the process-based
# engine configuration.
$repositoryRootDirectory = Split-Path -Parent $PSScriptRoot
$mavenCommand = Get-Command mvn -ErrorAction SilentlyContinue

if (-not $mavenCommand) {
  throw "Apache Maven is required to run the application. Install Maven and try again."
}

Push-Location $repositoryRootDirectory
try {
  if ($UseCobolProcess) {
    $cobolCompilerCommand = Get-Command cobc -ErrorAction SilentlyContinue

    if (-not $cobolCompilerCommand) {
      throw "GNU COBOL is required for -UseCobolProcess. Install GNU COBOL or run without that switch."
    }

    Write-Output "Compiling the COBOL payroll engine."
    & (Join-Path $repositoryRootDirectory "cobol-core/scripts/compile-payroll-engine.ps1")

    $compiledCobolExecutablePath = Join-Path $repositoryRootDirectory "cobol-core/build/payroll-calculation-engine.exe"

    Write-Output "Starting Spring Boot with the real COBOL process integration."
    & mvn -pl spring-control-client spring-boot:run `
      "-Dspring-boot.run.arguments=--company-payroll.execution-mode=process,--company-payroll.cobol-executable-path=$compiledCobolExecutablePath"
  } else {
    Write-Output "Starting Spring Boot in demonstration mode."
    & mvn -pl spring-control-client spring-boot:run
  }
} finally {
  Pop-Location
}
