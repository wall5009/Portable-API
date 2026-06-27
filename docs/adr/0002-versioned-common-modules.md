# 0002: Versioned Common Modules Own Minecraft Differences

## Status

Accepted

## Context

Minecraft 1.20.1 and 1.21.1 differ in mappings, Java version, resource identifiers, components, networking, registries, and lifecycle details.

## Decision

The repository uses separate common modules for each Minecraft version:

- `common/mc1_20_1`
- `common/mc1_21_1`

These modules may import Minecraft classes but must not import Fabric, Forge, or NeoForge APIs.

## Consequences

- Version changes are isolated from loader bootstrap code.
- Loader modules stay thin.
- Common modules can evolve per Minecraft version without weakening public API compatibility.
