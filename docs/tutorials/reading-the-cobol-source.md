# Reading The COBOL Source

If you are new to COBOL, read the program from top to bottom in this order:

1. **Identification Division**: this names the program.
2. **Environment Division**: this describes the execution environment.
3. **Data Division**: this declares all variables and working storage.
4. **Procedure Division**: this contains the procedural business logic.

## What to look for

- All variable names are intentionally descriptive.
- Each paragraph is named after its job.
- Numeric conversions happen in one dedicated paragraph.
- The final display paragraph produces key-value output that the Java client can
  parse safely.

## Helpful tip

Read the Java process adapter after you understand the `DISPLAY` output in the
COBOL source. The adapter simply turns those output lines into structured JSON
for the REST response.
