# v1.1 Limitations

Portable API intentionally does not abstract block entities, menus, screens, renderers, entities, capabilities, attachments, tracking-specific network operations, or arbitrary common broadcast semantics.

Networking has a complete bounded core codec, registration contract, and native payload send/receive bridges for Fabric 1.20.1, Fabric 1.21.1, Forge 1.20.1, and NeoForge 1.21.1. The portable contract covers client-to-server sends and server-to-one-player sends. Arbitrary broadcast, player tracking, login/configuration-phase payloads, and loader-specific connection operations remain platform-specific.

Only global config files are portable in v1.1. Per-world/server config scopes remain loader-specific.

Client tick is wired across all four targets through client-only loader bridges. Common code may register client-tick callbacks, but callbacks must still avoid dedicated-server-only assumptions and client-only classes must stay out of dedicated-server code paths.
