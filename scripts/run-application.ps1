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

function Test-MappedNetworkDrive {
  param(
    [Parameter(Mandatory = $true)]
    [string]$PathToCheck
  )

  $driveQualifier = Split-Path -Qualifier $PathToCheck
  if (-not $driveQualifier) {
    return $false
  }

  $driveName = $driveQualifier.TrimEnd('\').TrimEnd(':')
  $powerShellDrive = Get-PSDrive -Name $driveName -ErrorAction SilentlyContinue
  return ($null -ne $powerShellDrive -and -not [string]::IsNullOrWhiteSpace($powerShellDrive.DisplayRoot))
}

function New-LocalMavenWorkspaceCopy {
  param(
    [Parameter(Mandatory = $true)]
    [string]$SourceRepositoryRootDirectory
  )

  $localWorkspaceDirectory = Join-Path $env:TEMP "kokiki-local-maven-workspace"

  if (Test-Path -LiteralPath $localWorkspaceDirectory) {
    Remove-Item -LiteralPath $localWorkspaceDirectory -Recurse -Force
  }

  New-Item -ItemType Directory -Path $localWorkspaceDirectory -Force | Out-Null

  Get-ChildItem -LiteralPath $SourceRepositoryRootDirectory -Force | Where-Object {
    $_.Name -notin @(".git", "target", "build")
  } | ForEach-Object {
    Copy-Item -LiteralPath $_.FullName -Destination $localWorkspaceDirectory -Recurse -Force
  }

  Write-Warning "The repository is on a mapped network drive, so Spring Boot will run from a temporary local workspace copy at $localWorkspaceDirectory."
  return $localWorkspaceDirectory
}

if (-not $mavenCommand) {
  throw "Apache Maven is required to run the application. Install Maven and try again."
}

$mavenExecutionDirectory = if (Test-MappedNetworkDrive -PathToCheck $repositoryRootDirectory) {
  New-LocalMavenWorkspaceCopy -SourceRepositoryRootDirectory $repositoryRootDirectory
} else {
  $repositoryRootDirectory
}
$rootPomPath = Join-Path $mavenExecutionDirectory "pom.xml"

Push-Location $mavenExecutionDirectory
try {
  if ($UseCobolProcess) {
    $cobolCompilerCommand = Get-Command cobc -ErrorAction SilentlyContinue

    if (-not $cobolCompilerCommand) {
      throw "GNU COBOL is required for -UseCobolProcess. Install GNU COBOL or run without that switch."
    }

    Write-Output "Compiling the COBOL payroll engine."
    & (Join-Path $mavenExecutionDirectory "cobol-core/scripts/compile-payroll-engine.ps1")
    if ($LASTEXITCODE -ne 0) {
      throw "GNU COBOL compilation failed."
    }

    $compiledCobolExecutablePath = Join-Path $mavenExecutionDirectory "cobol-core/build/payroll-calculation-engine.exe"

    Write-Output "Starting Spring Boot with the real COBOL process integration."
    & mvn "-f" $rootPomPath "-pl" "spring-control-client" "spring-boot:run" `
      "-Dspring-boot.run.arguments=--company-payroll.execution-mode=process,--company-payroll.cobol-executable-path=$compiledCobolExecutablePath"
    if ($LASTEXITCODE -ne 0) {
      throw "Spring Boot failed to start."
    }
  } else {
    Write-Output "Starting Spring Boot in demonstration mode."
    & mvn "-f" $rootPomPath "-pl" "spring-control-client" "spring-boot:run"
    if ($LASTEXITCODE -ne 0) {
      throw "Spring Boot failed to start."
    }
  }
} finally {
  Pop-Location
}
