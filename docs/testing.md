# Testing

Fast verification:

```powershell
.\gradlew.bat check
```

Release verification:

```powershell
.\gradlew.bat clean fullDistributionBuild
```

Important tasks:

- `checkApiCompatibility`
- `validateGeneratedResources`
- `dependencyBoundaryCheck`
- `verifyArtifacts`
- `generatePublicationChecksums`
- `fullDistributionBuild`

Current deterministic coverage includes core unit tests for identifiers, lifecycle ordering, content duplicate checks, packet codecs and bounds, typed config recovery and reload, command trees, and structured data builders.

Loader GameTest and headless client automation remain a documented v1.1 CI boundary. The build validates compilation, metadata, resource generation, API compatibility, and artifact layout deterministically.
