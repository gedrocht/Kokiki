#!/usr/bin/env bash

set -euo pipefail

# This script gives beginners one obvious way to start the application. By
# default it runs the Spring Boot client in demonstration mode. When the caller
# passes --use-cobol-process and GNU COBOL is installed, the script compiles the
# COBOL engine first and then starts Spring Boot with the process-based engine
# configuration.
repository_root_directory="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
root_pom_path="${repository_root_directory}/pom.xml"
use_cobol_process="false"

if [[ "${1:-}" == "--use-cobol-process" ]]; then
  use_cobol_process="true"
fi

if ! command -v mvn >/dev/null 2>&1; then
  printf 'Apache Maven is required to run the application. Install Maven and try again.\n' >&2
  exit 1
fi

cd "${repository_root_directory}"

if [[ "${use_cobol_process}" == "true" ]]; then
  if ! command -v cobc >/dev/null 2>&1; then
    printf 'GNU COBOL is required for --use-cobol-process. Install GNU COBOL or run without that flag.\n' >&2
    exit 1
  fi

  printf 'Compiling the COBOL payroll engine.\n'
  bash "${repository_root_directory}/cobol-core/scripts/compile-payroll-engine.sh"

  printf 'Starting Spring Boot with the real COBOL process integration.\n'
  mvn -f "${root_pom_path}" -pl spring-control-client spring-boot:run \
    "-Dspring-boot.run.arguments=--company-payroll.execution-mode=process,--company-payroll.cobol-executable-path=${repository_root_directory}/cobol-core/build/payroll-calculation-engine"
else
  printf 'Starting Spring Boot in demonstration mode.\n'
  mvn -f "${root_pom_path}" -pl spring-control-client spring-boot:run
fi
