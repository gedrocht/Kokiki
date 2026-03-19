#!/usr/bin/env bash

set -euo pipefail

# This script gives the repository a single obvious build entrypoint from the
# top-level scripts folder. It compiles the COBOL engine when GNU COBOL is
# available and then runs the Maven verification pipeline for the Java module.
repository_root_directory="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
root_pom_path="${repository_root_directory}/pom.xml"
compiled_cobol_executable_path="${repository_root_directory}/cobol-core/build/payroll-calculation-engine"

get_java_major_version() {
  local java_version_line

  java_version_line="$(java -version 2>&1 | head -n 1)"
  if [[ "${java_version_line}" =~ \"([0-9]+)(\.[0-9]+)? ]]; then
    printf '%s' "${BASH_REMATCH[1]}"
    return 0
  fi

  return 1
}

cd "${repository_root_directory}"

maven_arguments=(-f "${root_pom_path}" -B -ntp -pl spring-control-client -am clean verify)

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

if ! command -v java >/dev/null 2>&1; then
  printf 'Java is required to build this repository. Install Java 21 and try again.\n' >&2
  exit 1
fi

java_major_version="$(get_java_major_version || true)"
if [[ -z "${java_major_version}" ]]; then
  printf 'Java is required to build this repository. Install Java 21 and try again.\n' >&2
  exit 1
fi

if [[ "${java_major_version}" != "21" ]]; then
  printf 'Java %s was detected. The full local build currently requires Java 21 because PMD and ArchUnit in this repository do not yet support newer class file versions reliably. Install Temurin 21, set JAVA_HOME to that JDK, reopen your shell, and try again.\n' "${java_major_version}" >&2
  exit 1
fi

printf 'Running Maven verification.\n'
mvn "${maven_arguments[@]}"
