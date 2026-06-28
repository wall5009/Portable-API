# API Usage

Implement `PortableMod` in shared code. Register only portable declarations there.

```java
context.content().registerItem("portable_item", PortableItemSettings.defaults());
context.config().register(new PortableConfigSpec("my_mod.properties", "enabled=true\n"));
context.networking().declareChannel("main", NetworkPhase.PLAY, 1, 32767);
```

Use lifecycle callbacks for stable cross-loader events:

```java
context.lifecycle().onCommonSetup(() -> context.logger().info("Ready"));
context.lifecycle().onServerStarting(server -> context.logger().info(server.worldDirectory().toString()));
```

Put anything more specific in target modules. Examples include custom block entities, payload codecs, renderer registration, custom commands with complex Brigadier arguments, and loader-specific config screens.
