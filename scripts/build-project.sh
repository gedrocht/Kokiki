#!/usr/bin/env bash

set -euo pipefail

# This script gives the repository a single obvious build entrypoint from the
# top-level scripts folder. It compiles the COBOL engine when GNU COBOL is
# available and then runs the Maven verification pipeline for the Java module.
repository_root_directory="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
compiled_cobol_executable_path="${repository_root_directory}/cobol-core/build/payroll-calculation-engine"

cd "${repository_root_directory}"

maven_arguments=(-B -ntp clean verify)

if command -v cobc >/dev/null 2>&1; then
  printf 'GNU COBOL detected. Compiling the COBOL payroll engine first.\n'
  bash "${repository_root_directory}/cobol-core/scripts/compile-payroll-engine.sh"
  maven_arguments+=("-Dcobol.payroll.executable.path=${compiled_cobol_executable_path}")
else
  printf 'GNU COBOL was not found. The Java build will continue in demonstration mode without the real COBOL integration test.\n' >&2
fi

if ! command -v mvn >/dev/null 2>&1; then
  printf 'Apache Maven is required to build this repository. Install Maven and try again.\n' >&2
  exit 1
fi

printf 'Running Maven verification.\n'
mvn "${maven_arguments[@]}"
