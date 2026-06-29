# v1.1 Limitations

Portable API intentionally does not abstract block entities, menus, screens, renderers, entities, capabilities, attachments, tracking-specific network operations, or arbitrary common broadcast semantics.

Networking has a complete bounded core codec and registration contract. Native loader payload send/receive bridges remain a documented boundary because Fabric 1.20.1, Fabric 1.21.1, Forge 1.20.1, and NeoForge 1.21.1 use materially different payload registration APIs.

Only global config files are portable in v1.1. Per-world/server config scopes remain loader-specific.

Client tick remains a core event surface, but v1.1 does not wire it as a dedicated-server-safe portable bridge across all four targets.
