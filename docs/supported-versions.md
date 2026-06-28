# Supported Versions

| Target | Minecraft | Loader/plugin pins | Java |
| --- | --- | --- | --- |
| Fabric 1.20.1 | `1.20.1` | Fabric Loader `0.19.3`, Fabric API `0.92.9+1.20.1`, Loom `1.13.6` | 17 |
| Forge 1.20.1 | `1.20.1` | Forge `1.20.1-47.4.20`, ModDevGradle `2.0.141` legacy Forge plugin | 17 |
| Fabric 1.21.1 | `1.21.1` | Fabric Loader `0.19.3`, Fabric API `0.116.12+1.21.1`, Loom `1.13.6` | 21 |
| NeoForge 1.21.1 | `1.21.1` | NeoForge `21.1.234`, ModDevGradle `2.0.141` | 21 |

These pins were verified from official Maven metadata on 2026-06-28. Loom `1.13.6` is intentionally used because newer Loom metadata lines target Gradle 9.x, while this repository pins Gradle 8.14.3 for Forge-family compatibility.
