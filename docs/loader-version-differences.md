# Loader and Version Differences

## Java Versions

- Minecraft 1.20.1 targets compile with Java 17 bytecode.
- Minecraft 1.21.1 targets compile with Java 21 bytecode.
- CI runs on JDK 21 and uses Gradle toolchains.

## Mappings

The build uses Architectury Loom with official Mojang mappings for all Minecraft source sets. Fabric API dependencies are remapped by Loom for the selected mappings.

## Fabric

Fabric exposes entrypoints rather than a Forge-style common setup event. Portable API installs during its main entrypoint and wires Fabric server lifecycle and command registration events.

## Forge 1.20.1

Forge uses `mods.toml`, `@Mod`, the mod event bus for setup events, and the Forge event bus for server and command events.

## NeoForge 1.21.1

NeoForge uses `neoforge.mods.toml`, `@Mod`, constructor-injected `IEventBus`, and the NeoForge event bus. Its `javafml` loader version range is `[1,)`.

## Registry Safety

Registry timing and registry ownership differ between loaders and versions. v1 exposes stable keys and suppliers, but raw writes are limited until each typed registry adapter is designed and tested.
