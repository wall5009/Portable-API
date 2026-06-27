# Public API Reference

All stable public APIs are under `dev.portablemc.api`.

## Initialization

- `PortableApi.initialize(String modId, PortableInitializer initializer)` creates a mod-scoped context.
- `ModContext` exposes service interfaces.
- `PortableApi.get()` returns the installed runtime for diagnostics and capability checks.

## Runtime and Capabilities

- `PlatformInfo` reports loader, Minecraft version, physical side, production mode, loader version when known, and loaded mod checks.
- `CapabilityReport` reports `SUPPORTED`, `LIMITED`, `DEFERRED`, or `UNSUPPORTED`.
- `UnsupportedFeatureException` is thrown for unsupported or deferred calls.

## Services

- `LifecycleEvents`: common setup, client setup, server starting, server started, server stopping.
- `PortableLogger`: small logger facade.
- `SidedExecutor`: client/server physical side execution helpers.
- `ConfigService`: properties-backed v1 config declarations.
- `CommandService`: literal command registration with portable invocation and message APIs.
- `RegistrationService`: deferred registry supplier creation and stable keys.
- `CreativeTabService`: creative tab population declarations.
- `NetworkService`: channel and packet declarations.
- `ResourceService`: reload listener declarations.
- `AttachmentService`: attachment declarations; persistent storage is deferred in v1.

## Identifier and Registry Types

`Identifier` is strict lowercase `namespace:path`. Invalid or uppercase identifiers are rejected instead of normalized silently.

`RegistryKey<T>` and `RegistrySupplier<T>` are typed. Consumers can use them as stable handles even when raw registry writes are limited.

## API Expansion Rules

Interfaces are designed for additive expansion through new sibling interfaces, default methods, and service capability checks. Public method removals, signature changes, and enum constant removals are not allowed in 1.x.
