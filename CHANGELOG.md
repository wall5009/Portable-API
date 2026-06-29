# Changelog

## 1.1.0 - 2026-06-29

### Added

- API stability annotations and checked-in v1.0.0 API compatibility validation.
- Bounded typed packet codecs, packet types, packet registrations, dispatch validation, native Fabric, Forge, and NeoForge payload bridges, and fake-adapter contract tests.
- Typed config specs with validated booleans, numbers, strings, enums, bounded lists, immutable snapshots, recovery warnings, atomic saves, reload listeners, and thread-safe handles.
- Portable command tree declarations with nested literals, typed arguments, suggestions, source helpers, and Brigadier bridges for Minecraft 1.20.1 and 1.21.1.
- Lifecycle hooks for server started/stopping/stopped, server tick, client tick, and reload callbacks in addition to the v1.0.0 hooks.
- Structured deterministic data builders for common JSON resources.
- Platform service queries for mod-loaded checks, optional mod versions, development environment detection, and paths.
- A minimal v1.1 template demonstrating content, creative-tab placement, command trees, typed config, portable C2S and S2C packet handling, lifecycle, and generated resources.

### Changed

- Project version is now `1.1.0`.
- Repository and generated metadata now use All Rights Reserved terms.
- Template scaffold copying permission is documented separately in `LICENSE`.

### Verification

- Fabric 1.20.1 and Fabric 1.21.1 now run real Fabric GameTest server automation.
- Forge 1.20.1 and NeoForge 1.21.1 GameTest tasks execute real template tests.
- Dedicated-server smoke tasks launch all four targets and exit only after portable server lifecycle ticks fire.
- Headless client validation launches all four targets and exits only after portable client tick wiring fires.

### Boundaries

- Complex systems such as block entities, menus, renderers, entities, capabilities, attachments, and tracking-specific networking remain outside the common API.
- Arbitrary broadcast, player tracking, and connection-phase-specific networking remain loader-specific unless represented by the portable C2S or single-player S2C packet contract.
