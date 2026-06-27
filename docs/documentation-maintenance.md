# Documentation Maintenance

Documentation must evolve with the API.

## Required Updates

Update docs when:

- A public type or method is added.
- Capability status changes.
- A target is added or removed.
- Publication coordinates change.
- Build, setup, or CI behavior changes.
- An unsupported feature becomes supported or limited.

## Link Rules

Relative links are checked by `checkDocsLinks`. Prefer relative links for repository docs and stable official links for external specifications.

## ADR Rules

Create an ADR for:

- New module boundaries.
- New public API families.
- Loader or mapping strategy changes.
- Compatibility policy changes.
- Decisions to defer or reject major Minecraft systems.
