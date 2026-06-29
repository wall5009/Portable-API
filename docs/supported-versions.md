# Supported Versions

| Target | Module | Java bytecode | Loader dependency |
|---|---|---:|---|
| Minecraft 1.20.1 Fabric | `platform-fabric-1201` | 17 | Fabric Loader `0.19.3`, Fabric API `0.92.9+1.20.1` |
| Minecraft 1.20.1 Forge | `platform-forge-1201` | 17 | Forge `1.20.1-47.4.20` |
| Minecraft 1.21.1 Fabric | `platform-fabric-1211` | 21 | Fabric Loader `0.19.3`, Fabric API `0.116.12+1.21.1` |
| Minecraft 1.21.1 NeoForge | `platform-neoforge-1211` | 21 | NeoForge `21.1.234` |

Gradle runs on Java 21. Toolchains and `options.release` keep 1.20.1 modules on Java 17.

Pinned dependency versions are recorded in `gradle.properties`; the verification sources and date are listed in `dependency-versions.md`.
