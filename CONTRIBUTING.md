# Contributing

Portable API is an All Rights Reserved repository. Contributions are accepted only for PortableMC-maintained development; submitting changes does not grant redistribution or sublicensing rights.

## Requirements

- Run Gradle with Java 21.
- Preserve Java 17 compilation for Minecraft 1.20.1 modules.
- Keep `api-core` free of Minecraft and loader dependencies.
- Keep Minecraft-version code in `api-mc-*`.
- Keep loader-specific code in `platform-*`.
- Do not publish template jars as Portable API runtime downloads.

## Verification

Before proposing changes, run:

```powershell
.\gradlew.bat check
.\gradlew.bat validateGeneratedResources
.\gradlew.bat verifyArtifacts
```

Release work must run:

```powershell
.\gradlew.bat clean fullDistributionBuild
```

## API Compatibility

`checkApiCompatibility` compares `api-core` against `config/api/v1.0.0-api.txt`. Public v1.0.0 methods should be retained. Prefer additive APIs, default interface methods, deprecations, and compatibility shims over removals.

## License Headers

Every Java source file must start with:

```java
/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
```
