# Portable API

Portable API is a v1 Minecraft interoperability API for maintaining one loader-neutral consumer codebase while targeting these runtimes:

| Loader | Minecraft | Java | Artifact |
| --- | --- | ---: | --- |
| Fabric | 1.20.1 | 17 | `dev.portablemc:portable-api-fabric-1.20.1` |
| Forge | 1.20.1 | 17 | `dev.portablemc:portable-api-forge-1.20.1` |
| Fabric | 1.21.1 | 21 | `dev.portablemc:portable-api-fabric-1.21.1` |
| NeoForge | 1.21.1 | 21 | `dev.portablemc:portable-api-neoforge-1.21.1` |

The public API lives in `dev.portablemc.api`. Internal implementation packages live under `dev.portablemc.internal` and are not compatibility-guaranteed.

## What v1 Provides

- Loader-neutral initialization through `PortableApi.initialize`.
- Stable service interfaces for lifecycle events, logging, environment detection, sided execution, configuration, commands, resource reload declarations, packet declarations, registry suppliers, creative-tab population declarations, and capability reporting.
- Versioned Minecraft common modules for 1.20.1 and 1.21.1.
- Thin platform modules for Fabric, Forge, and NeoForge.
- Blank validation consumer mods for each supported target.
- Gradle checks that prevent loader dependencies from leaking into API/common modules.
- CI that builds, tests, validates docs, and publishes distinct artifacts.

## Intentional v1 Limits

Portable API v1 is a foundation, not a full replacement for every loader API. Rendering, screens, world generation, bytecode transformation, and persistent data attachments/components are intentionally deferred. Raw registry writes are marked limited until typed registry adapters can be proven safe per registry family.

See [supported features](docs/features.md) for exact status.

## Quick Commands

```powershell
./gradlew.bat validateAllTargets
./gradlew.bat publishAllPublicationsToLocalStagingRepository
```

Use JDK 21 for the full matrix. The 1.20.1 projects still compile with `--release 17`.
The repository includes `gradle/gradle-daemon-jvm.properties` so wrapper launches can select a Java 21 Gradle daemon when one is installed.

## Documentation

- [Architecture](docs/architecture.md)
- [Repository layout](docs/repository-layout.md)
- [Setup](docs/setup.md)
- [Dependency configuration](docs/dependencies.md)
- [Public API reference](docs/public-api.md)
- [Supported feature matrix](docs/features.md)
- [Loader and version differences](docs/loader-version-differences.md)
- [Migration procedures](docs/migration.md)
- [Extension guidelines](docs/extensions.md)
- [Compatibility guarantees](docs/compatibility.md)
- [Testing](docs/testing.md)
- [Publishing](docs/publishing.md)
- [Troubleshooting](docs/troubleshooting.md)
- [Documentation maintenance](docs/documentation-maintenance.md)
- [Contribution standards](CONTRIBUTING.md)
- [ADRs](docs/adr/README.md)
