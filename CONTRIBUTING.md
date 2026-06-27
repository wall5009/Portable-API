# Contribution Standards

Portable API treats the public package as a compatibility contract. Changes must be small, documented, and backed by tests.

## Rules

- Keep public APIs in `dev.portablemc.api`.
- Keep implementation in `dev.portablemc.internal`.
- Do not import Fabric, Forge, NeoForge, or versioned Minecraft classes from `portable-api`, `runtime-core`, or any public API type.
- Do not expose loader classes or Minecraft classes in public signatures unless an ADR explicitly approves it.
- Add or update docs for any public API change.
- Add an ADR for major surface, module, or compatibility decisions.
- Add tests for changed behavior and update target descriptors when capability status changes.

## Review Checklist

- `./gradlew.bat validateAllTargets` passes.
- `./gradlew.bat spotlessCheck` passes.
- New public APIs have Javadocs and preserve binary-compatible expansion.
- Unsupported behavior is represented through `CapabilityReport` and explicit exceptions.
- Documentation links pass `checkDocsLinks`.
