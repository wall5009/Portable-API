# Dependency Configuration

Portable API publishes one public API artifact and one runtime artifact per supported loader/version target.

## Consumer Dependency Rule

Consumers should depend on the target platform artifact for their loader and Minecraft version. They should compile against `dev.portablemc.api` only.

## Coordinates

| Target | Coordinate |
| --- | --- |
| API specification | `dev.portablemc:portable-api:1.0.0` |
| Fabric 1.20.1 | `dev.portablemc:portable-api-fabric-1.20.1:1.0.0` |
| Forge 1.20.1 | `dev.portablemc:portable-api-forge-1.20.1:1.0.0` |
| Fabric 1.21.1 | `dev.portablemc:portable-api-fabric-1.21.1:1.0.0` |
| NeoForge 1.21.1 | `dev.portablemc:portable-api-neoforge-1.21.1:1.0.0` |

## Loader Metadata

Consumer mods should declare a required dependency on `portable_api` in their loader metadata. The validation modules show the minimum metadata required, but they are not templates.
