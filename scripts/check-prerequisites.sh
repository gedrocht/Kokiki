#!/usr/bin/env bash

set -euo pipefail

# This script is intentionally beginner-focused. It checks for the tools that
# the repository needs and prints the exact next steps that a new contributor
# should take.
check_tool() {
  local tool_name="$1"
  local command_name="$2"
  local download_url="$3"

  if command -v "${command_name}" >/dev/null 2>&1; then
    printf '[FOUND] %s\n' "${tool_name}"
  else
    printf '[MISSING] %s\n' "${tool_name}"
    printf '          Download: %s\n' "${download_url}"
  fi
}

printf '\nKokiki prerequisite check\n'
printf '=========================\n\n'

check_tool "Git" "git" "https://git-scm.com/downloads"
check_tool "Java 21 or newer" "java" "https://adoptium.net/"
check_tool "Apache Maven" "mvn" "https://maven.apache.org/download.cgi"
check_tool \
  "GNU COBOL (optional but recommended for the real COBOL integration path)" \
  "cobc" \
  "https://gnucobol.sourceforge.io/"

printf '\nRecommended beginner workflow\n'
printf '%s\n' '-----------------------------'
printf '1. Build the project:\n'
printf '   bash scripts/build-project.sh\n'
printf '2. Run the application:\n'
printf '   bash scripts/run-application.sh\n'
printf '3. Run the tests:\n'
printf '   bash scripts/test-project.sh\n'
