# Architecture

Portable API is split into four layers.

## Public API

Module: `:portable-api`

The public API is pure Java. It contains:

- `PortableApi` static entry point.
- `ModContext` and `PortableInitializer`.
- Service contracts for lifecycle, logging, config, commands, resources, networking, registries, creative tabs, sided execution, and attachments.
- Value objects such as `Identifier`, `RegistryKey`, and `SemanticVersion`.
- Capability reporting through `CapabilityReport`.

No public type imports Fabric, Forge, NeoForge, or Minecraft classes.

## Runtime Core

Module: `:runtime-core`

The runtime core implements loader-neutral behavior:

- Stores lifecycle callbacks.
- Stores command and packet declarations.
- Provides properties-backed config defaults.
- Provides registry suppliers and explicit limited raw registry behavior.
- Builds mod-scoped contexts.

This module is internal and is bundled into each platform artifact.

## Versioned Common Modules

Modules:

- `:common:mc1_20_1`
- `:common:mc1_21_1`

These modules are allowed to import version-specific Minecraft classes but not loader APIs. They define target capability profiles and native identifier conversion.

## Platform Modules

Modules:

- `:platforms:fabric-1.20.1`
- `:platforms:forge-1.20.1`
- `:platforms:fabric-1.21.1`
- `:platforms:neoforge-1.21.1`

Platform modules are intentionally thin. They install the matching runtime, collect platform identity, wire lifecycle/server/command events, and publish loader-specific mod metadata.

## Consumer Validation

The only consumer code in the repository is the blank validation mod under `validation/*`. It verifies downstream compilation and loader metadata without demonstrating content or creating templates.
