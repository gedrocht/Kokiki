#!/usr/bin/env bash

set -euo pipefail

# This script gives beginners one obvious test command. It runs the repository
# policy validation first and then runs the Maven verification pipeline. If GNU
# COBOL is installed, the build script will automatically include the real COBOL
# integration path.
repository_root_directory="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

cd "${repository_root_directory}"

printf 'Running repository policy validation.\n'
if command -v pwsh >/dev/null 2>&1; then
  pwsh -File "${repository_root_directory}/scripts/validate-repo.ps1"
elif command -v powershell >/dev/null 2>&1; then
  powershell -ExecutionPolicy Bypass -File "${repository_root_directory}/scripts/validate-repo.ps1"
else
  printf 'PowerShell was not found, so repository policy validation is being skipped locally.\n' >&2
fi

printf 'Running full project build and test verification.\n'
bash "${repository_root_directory}/scripts/build-project.sh"
