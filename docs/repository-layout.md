# Repository Layout

| Path | Purpose |
| --- | --- |
| `portable-api/` | Public API specification and API unit tests. |
| `runtime-core/` | Internal Java-only runtime services shared by every target. |
| `common/mc1_20_1/` | Minecraft 1.20.1 common adapter layer. |
| `common/mc1_21_1/` | Minecraft 1.21.1 common adapter layer. |
| `platforms/fabric-1.20.1/` | Fabric 1.20.1 mod artifact. |
| `platforms/forge-1.20.1/` | Forge 1.20.1 mod artifact. |
| `platforms/fabric-1.21.1/` | Fabric 1.21.1 mod artifact. |
| `platforms/neoforge-1.21.1/` | NeoForge 1.21.1 mod artifact. |
| `validation/*` | Blank consumer mods used only for compile and launch validation. |
| `docs/` | Maintained project documentation. |
| `docs/adr/` | Architecture decision records. |
| `.github/workflows/` | CI definitions. |

The platform artifacts bundle `portable-api`, `runtime-core`, and the matching `common` module so consumers depend on one target artifact at runtime.
