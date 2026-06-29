# Portable API

Portable API is a small cross-version, cross-loader foundation for Minecraft mods.

Version `1.1.0` supports:

| Minecraft | Loader | Java |
|---|---|---|
| 1.20.1 | Fabric | 17 |
| 1.20.1 | Forge | 17 |
| 1.21.1 | Fabric | 21 |
| 1.21.1 | NeoForge | 21 |

## Modules

Only the seven API modules are Maven API publications:

- `api-core`
- `api-mc-1201`
- `api-mc-1211`
- `platform-fabric-1201`
- `platform-forge-1201`
- `platform-fabric-1211`
- `platform-neoforge-1211`

The template modules are developer scaffolding and release-test artifacts, not player-facing API downloads.

## v1.1 Highlights

- API stability annotations: public, experimental, internal, and since-version markers.
- API compatibility check against `config/api/v1.0.0-api.txt`.
- Bounded typed packet codecs, registration contracts, and native C2S/S2C payload bridges.
- Typed config files with validation, immutable snapshots, recovery, atomic save, and reload listeners.
- Portable command trees with nested literals, typed arguments, source feedback, and suggestions.
- Expanded lifecycle hooks for setup, server start/stop, server ticks, client ticks, reload, data generation.
- Structured deterministic resource builders for common language, model, blockstate, tag, recipe, loot, and pack metadata JSON.
- Platform service queries for loader, version, side, paths, mod-loaded checks, optional mod versions, and development detection.
- All Rights Reserved licensing with a narrow template scaffold permission in `LICENSE`.

## Build

Gradle must run on Java 21 because the pinned Loom plugin requires it. The build still compiles Java 17 bytecode for 1.20.1 modules and Java 21 bytecode for 1.21.1 modules.

```powershell
$env:JAVA_HOME='C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
.\gradlew.bat check
.\gradlew.bat runtimeAutomation
.\gradlew.bat fullDistributionBuild
```

## Distribution

Use `fullDistributionBuild` or `releaseBuild`. The player-facing files are the four platform runtime jars under the generated CurseForge and Modrinth folders. Template jars stay under developer artifacts.

## Documentation

Start with `docs/index.md`. The v1.0.0 to v1.1.0 migration guide is `docs/migration-1.0.0-to-1.1.0.md`, and pinned dependency verification is in `docs/dependency-versions.md`.

## License

Portable API is Copyright (c) 2026 PortableMC. All Rights Reserved. See `LICENSE` for the exact terms and the separate, narrow permission for copying only the template scaffold files.
