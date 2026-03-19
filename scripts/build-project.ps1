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

function Get-JavaMajorVersion {
  $javaVersionOutputLine = cmd /c "java -version 2>&1" | Select-Object -First 1
  if ($javaVersionOutputLine -match '"(?<major>\d+)(\.\d+)?') {
    return [int]$Matches.major
  }

  return $null
}

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

  Write-Warning "The repository is on a mapped network drive, so Maven will run from a temporary local workspace copy at $localWorkspaceDirectory."
  return $localWorkspaceDirectory
}

if (-not $mavenCommand) {
  throw "Apache Maven is required to build this repository. Install Maven and try again."
}

$javaMajorVersion = Get-JavaMajorVersion
if ($null -ne $javaMajorVersion -and $javaMajorVersion -gt 21) {
  Write-Warning "Java $javaMajorVersion was detected. Local static-analysis tools in this repository are validated most reliably on Java 21, which is also what GitHub Actions uses."
}

$mavenExecutionDirectory = if (Test-MappedNetworkDrive -PathToCheck $repositoryRootDirectory) {
  New-LocalMavenWorkspaceCopy -SourceRepositoryRootDirectory $repositoryRootDirectory
} else {
  $repositoryRootDirectory
}
$rootPomPath = Join-Path $mavenExecutionDirectory "pom.xml"

Push-Location $mavenExecutionDirectory
try {
  $mavenArguments = @("-f", $rootPomPath, "-B", "-ntp", "-pl", "spring-control-client", "-am", "clean", "verify")

  if ($cobolCompilerCommand) {
    Write-Output "GNU COBOL detected. Compiling the COBOL payroll engine first."
    & (Join-Path $mavenExecutionDirectory "cobol-core/scripts/compile-payroll-engine.ps1")
    if ($LASTEXITCODE -ne 0) {
      throw "GNU COBOL compilation failed."
    }

    $compiledCobolExecutablePath = Join-Path $mavenExecutionDirectory "cobol-core/build/payroll-calculation-engine.exe"
    $mavenArguments += "-Dcobol.payroll.executable.path=$compiledCobolExecutablePath"
  } else {
    Write-Warning "GNU COBOL was not found. The Java build will continue in demonstration mode without the real COBOL integration test."
  }

  Write-Output "Running Maven verification."
  & mvn @mavenArguments
  if ($LASTEXITCODE -ne 0) {
    throw "Maven verification failed."
  }
} finally {
  Pop-Location
}
