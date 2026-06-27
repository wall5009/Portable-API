# 0001: Public API Remains Loader-Neutral

## Status

Accepted

## Context

Portable API targets Fabric, Forge, and NeoForge across two Minecraft version lines. Consumer code should not need direct loader dependencies for stable primitives.

## Decision

All stable public APIs are kept under `dev.portablemc.api` and must not import loader APIs or Minecraft classes. Loader and Minecraft integration lives in internal modules.

## Consequences

- Consumers can share one common codebase for supported primitives.
- Some Minecraft systems need typed abstractions before they can be exposed.
- Raw escape hatches are kept internal unless an ADR approves a public design.
