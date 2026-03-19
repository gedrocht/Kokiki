#!/usr/bin/env bash

set -euo pipefail

# This script compiles the free-format COBOL payroll engine into a single
# executable that the Spring Boot control client can launch as a subprocess.
repository_root_directory="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
build_output_directory="${repository_root_directory}/cobol-core/build"
source_program_file_path="${repository_root_directory}/cobol-core/src/main/cobol/payroll_calculation_engine.cbl"
compiled_executable_file_path="${build_output_directory}/payroll-calculation-engine"

mkdir -p "${build_output_directory}"
cobc -x -free -o "${compiled_executable_file_path}" "${source_program_file_path}"

printf 'Compiled COBOL payroll engine to %s\n' "${compiled_executable_file_path}"
