# Changelog

## 1.1.0 - 2026-06-29

### Added

- API stability annotations and checked-in v1.0.0 API compatibility validation.
- Bounded typed packet codecs, packet types, packet registrations, dispatch validation, and fake-adapter contract tests.
- Typed config specs with validated booleans, numbers, strings, enums, bounded lists, immutable snapshots, recovery warnings, atomic saves, reload listeners, and thread-safe handles.
- Portable command tree declarations with nested literals, typed arguments, suggestions, source helpers, and Brigadier bridges for Minecraft 1.20.1 and 1.21.1.
- Lifecycle hooks for server started/stopping/stopped, server tick, and reload callbacks in addition to the v1.0.0 hooks.
- Structured deterministic data builders for common JSON resources.
- Platform service queries for mod-loaded checks, optional mod versions, development environment detection, and paths.
- A minimal v1.1 template demonstrating content, creative-tab placement, command trees, typed config, packet registrations, lifecycle, and generated resources.

### Changed

- Project version is now `1.1.0`.
- Repository and generated metadata now use All Rights Reserved terms.
- Template scaffold copying permission is documented separately in `LICENSE`.

### Known Boundaries

- Loader adapters accept portable packet registrations, but full native payload send/receive bridges are still documented as platform-boundary work for loaders whose payload APIs differ materially.
- Client tick remains a core event surface but is not wired as a dedicated-server-safe portable bridge in v1.1.
- Complex systems such as block entities, menus, renderers, entities, capabilities, attachments, and tracking-specific networking remain outside the common API.
