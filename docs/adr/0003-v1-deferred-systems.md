# 0003: v1 Limits Raw Registry Writes and Persistent Attachments

## Status

Accepted

## Context

Registries, creative tabs, networking, resources, and attachments differ substantially by loader and Minecraft version. A broad abstraction in v1 would likely be fragile.

## Decision

Portable API v1 exposes stable declarations and capability reporting for these areas, but marks raw registry writes, creative tab population, networking sends, full resource reload wiring, and persistent attachments/components as limited or deferred where appropriate.

## Consequences

- v1 remains reliable and testable.
- Consumers get stable handles and clear unsupported-operation behavior.
- Future minor versions can add typed adapters without breaking existing code.
