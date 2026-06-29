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
- `runLoaderGameTests`
- `runDedicatedServerSmokeTests`
- `runHeadlessClientValidation`
- `runtimeAutomation`
- `fullDistributionBuild`

Current deterministic coverage includes core unit tests for identifiers, lifecycle ordering, content duplicate checks, packet codecs and bounds, typed config recovery and reload, command trees, and structured data builders.

Runtime automation includes real Fabric GameTest server tasks for Fabric 1.20.1 and Fabric 1.21.1, real Forge and NeoForge GameTest server tasks, dedicated-server smoke launches for all four targets, and headless client launches for all four targets. The smoke tasks exit only after the relevant portable lifecycle tick has fired.

On Linux CI, run release-grade tasks under `xvfb-run` so client validation can create a headless display:

```bash
xvfb-run --auto-servernum ./gradlew fullDistributionBuild --stacktrace --no-parallel
```

`fullDistributionBuild` depends on `runtimeAutomation`, artifact verification, generated-resource validation, API compatibility checks, and distribution layout checks.
