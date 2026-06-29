# v1.0.0 to v1.1.0 Migration

## Version

Update Portable API coordinates from `1.0.0` to `1.1.0`.

## Config

`PortableConfigSpec` still works but is deprecated. New code should use:

```java
PortableTypedConfigSpec.Builder builder = PortableTypedConfigSpec.builder("example.properties");
PortableConfigEntry<Boolean> enabled = builder.booleanValue("enabled", true, "Enables the feature.");
PortableConfigHandle handle = context.config().registerTyped(builder.build());
```

Read values from `handle.snapshot()`.

## Commands

`registerLiteral` still works. New nested commands should use `PortableCommandTree`.

## Networking

Replace channel-only declarations with `PortablePacketType<T>` and `PortablePacketCodec<T>` when the packet can use the portable bounded codec. v1.1 wires the portable C2S and single-player S2C packet contract through Fabric 1.20.1, Fabric 1.21.1, Forge 1.20.1, and NeoForge 1.21.1 native payload APIs.

## Lifecycle

Existing setup and server-starting callbacks remain. v1.1 adds server-started, stopping, stopped, server tick, client tick, and reload hooks. Client tick is wired by client-only loader bridges; keep client-only classes out of dedicated-server code paths.

## License

The repository is now All Rights Reserved. Review the root `LICENSE` before copying template files.
