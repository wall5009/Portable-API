# Publishing

`releaseBuild` stages Maven publications under `build/staging-maven` and writes SHA-256/SHA-512 checksums.

```powershell
.\gradlew.bat releaseBuild
```

The GitHub release workflow supports:

- Maven staging artifacts.
- GitHub Release uploads.
- Modrinth version creation for the template mod.
- CurseForge uploads for the template mod.
- Dry-run mode.

Required secrets:

| Secret | Purpose |
| --- | --- |
| `MAVEN_USERNAME` | Remote Maven username |
| `MAVEN_PASSWORD` | Remote Maven password/token |
| `MAVEN_PUBLISH_URL` | Remote Maven repository URL |
| `MODRINTH_TOKEN` | Modrinth token with version-write permission |
| `MODRINTH_PROJECT_ID` | Modrinth project id for the template mod |
| `MODRINTH_API_PROJECT_ID` | Modrinth project id for Portable API, used as a required dependency |
| `CURSEFORGE_TOKEN` | CurseForge upload token |
| `CURSEFORGE_PROJECT_ID` | CurseForge project id for the template mod |

Release channels should be one of `alpha`, `beta`, or `release`. Declare loaders and game versions per artifact; do not upload a Forge jar as a NeoForge file or a 1.20.1 jar as a 1.21.1 file.
