# Kokiki

Hardened repository baseline with strict GitHub quality, security, and review gates.

## Included safeguards

- Repository hygiene validation
- GitHub Actions linting and policy checks
- PowerShell static analysis
- Markdown and YAML linting
- Secret scanning
- Dependency review on pull requests
- Filesystem vulnerability and misconfiguration scanning
- Scheduled OSSF Scorecard checks

## Local validation

Run the repository policy checks locally with:

```powershell
pwsh -File scripts/validate-repo.ps1
```
