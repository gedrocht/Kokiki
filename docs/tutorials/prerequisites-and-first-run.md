# Prerequisites And First Run

This page is written for someone who wants the shortest path from zero context
to a running application.

## What you need

- Git:
  [https://git-scm.com/downloads](https://git-scm.com/downloads)
- Java 21 or newer:
  [https://adoptium.net/](https://adoptium.net/)
- Apache Maven:
  [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)
- GNU COBOL:
  [https://gnucobol.sourceforge.io/](https://gnucobol.sourceforge.io/)
  This one is optional if you only want demonstration mode.

## Fastest possible workflow

Important:
`.ps1` files are PowerShell scripts, not Python scripts.
Run them with `powershell -ExecutionPolicy Bypass -File ...` or directly from
PowerShell as `.\scripts\name.ps1`.

### Windows PowerShell

```powershell
powershell -ExecutionPolicy Bypass -File scripts/check-prerequisites.ps1
powershell -ExecutionPolicy Bypass -File scripts/build-project.ps1
powershell -ExecutionPolicy Bypass -File scripts/run-application.ps1
powershell -ExecutionPolicy Bypass -File scripts/test-project.ps1
```

### Bash

```bash
bash scripts/check-prerequisites.sh
bash scripts/build-project.sh
bash scripts/run-application.sh
bash scripts/test-project.sh
```

## If you want the real COBOL engine path

### Windows PowerShell

```powershell
powershell -ExecutionPolicy Bypass -File scripts/run-application.ps1 -UseCobolProcess
```

### Bash

```bash
bash scripts/run-application.sh --use-cobol-process
```
