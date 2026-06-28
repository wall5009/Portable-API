# Architecture

Portable API separates three concerns.

`api-core` contains the stable author-facing API. It has no Minecraft or loader types, which keeps shared mod code portable and testable with normal JUnit.

`api-mc-*` modules contain version-specific Minecraft adapters. The 1.20.1 and 1.21.1 adapters both create simple blocks and items, but they differ where Minecraft differs, such as `ResourceLocation` creation. This prevents unsafe differences from being hidden behind one misleading helper.

`platform-*` modules bridge the core SPI to each loader. Fabric registers directly with vanilla registries during initialization. Forge and NeoForge use deferred registers and event-bus callbacks.

The public bootstrap flow is explicit:

1. Loader entrypoint receives control from Fabric, Forge, or NeoForge.
2. Entrypoint calls the matching `*Bootstrap.initialize(...)`.
3. The bootstrap builds a `PortableModContext` with loader-backed services.
4. Shared code registers content, commands, config hooks, lifecycle callbacks, data generation, and network declarations.
5. Loader bridges fire lifecycle callbacks at the nearest stable equivalent.

Networking is declaration-only in V1. Payload send/receive APIs differ enough across these four targets that a universal payload abstraction would be misleading without a larger protocol design.
