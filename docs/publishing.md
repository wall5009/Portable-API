# Publishing

## Artifacts

The build publishes distinct artifact IDs:

- `portable-api`
- `portable-api-runtime-core`
- `portable-api-common-1.20.1`
- `portable-api-common-1.21.1`
- `portable-api-fabric-1.20.1`
- `portable-api-forge-1.20.1`
- `portable-api-fabric-1.21.1`
- `portable-api-neoforge-1.21.1`

Validation modules are not published.

## Local Staging

```powershell
./gradlew.bat publishAllPublicationsToLocalStagingRepository
```

Artifacts are written under `build/staging-repository`.

## Release Requirements

- `validateAllTargets` passes.
- `CHANGELOG.md` is updated.
- Version in `gradle.properties` follows semantic versioning.
- Documentation and ADRs reflect any public API changes.
