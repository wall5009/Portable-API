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

Replace channel-only declarations with `PortablePacketType<T>` and `PortablePacketCodec<T>` when the packet can use the portable bounded codec.

## Lifecycle

Existing setup and server-starting callbacks remain. v1.1 adds server-started, stopping, stopped, server tick, and reload hooks. The core client-tick event surface remains present, but v1.1 does not wire it as a dedicated-server-safe portable bridge across all targets.

## License

The repository is now All Rights Reserved. Review the root `LICENSE` before copying template files.
