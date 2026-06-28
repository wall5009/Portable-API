# Migration

Portable API is not a drop-in replacement for Architectury API, Fabric API, Forge, or NeoForge.

When moving a small mod:

1. Move loader-independent setup into a `PortableMod`.
2. Replace basic block/item registration with `PortableContentRegistry`.
3. Keep complex Minecraft object creation in version-specific modules.
4. Keep loader-only integrations in loader modules.
5. Use `PlatformInfo` only for explicit branching. Avoid hiding different behavior behind a common method.

Common migration traps:

- Do not move Fabric events into common code.
- Do not assume Forge deferred registers behave like Fabric direct registration.
- Do not assume 1.20.1 and 1.21.1 expose the same public constants or constructors.
- Do not treat V1 networking declarations as a payload API.
