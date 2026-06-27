# Supported Feature Matrix

| Feature | Fabric 1.20.1 | Forge 1.20.1 | Fabric 1.21.1 | NeoForge 1.21.1 | Notes |
| --- | --- | --- | --- | --- | --- |
| Initialization | Supported | Supported | Supported | Supported | `PortableApi.initialize`. |
| Lifecycle events | Supported | Supported | Supported | Supported | Fabric common/client setup timing is limited by Fabric entrypoint ordering. |
| Logging | Supported | Supported | Supported | Supported | Backed by `System.Logger`. |
| Environment detection | Supported | Supported | Supported | Supported | Loader, Minecraft, physical side, production mode. |
| Sided execution | Supported | Supported | Supported | Supported | Physical-side helpers only. |
| Configuration | Supported | Supported | Supported | Supported | Properties-backed v1 configs. |
| Commands | Supported | Supported | Supported | Supported | Literal commands with permission level and message output. |
| Registry suppliers | Supported | Supported | Supported | Supported | Stable typed handles. |
| Raw registry writes | Limited | Limited | Limited | Limited | Deferred until typed registry adapters are proven safe. |
| Creative tab population | Limited | Limited | Limited | Limited | Declarations exist; full item abstraction is deferred. |
| Resources | Limited | Limited | Limited | Limited | Listener declarations exist; full native reload wiring is deferred. |
| Networking | Limited | Limited | Limited | Limited | Packet declarations exist; send helpers are deferred. |
| Data attachments/components | Deferred | Deferred | Deferred | Deferred | Persistent storage differs across target lines. |
| Rendering | Deferred | Deferred | Deferred | Deferred | Explicitly outside v1. |
| Screens | Deferred | Deferred | Deferred | Deferred | Explicitly outside v1. |
| World generation | Deferred | Deferred | Deferred | Deferred | Requires a dedicated design. |
| Mixins/transformers | Deferred | Deferred | Deferred | Deferred | Not part of this API contract. |

`LIMITED` means the API surface is intentionally narrow but usable. `DEFERRED` means calls fail through capability checks or explicit unsupported-operation paths.
