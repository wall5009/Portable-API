# Portable API

Portable API is a small V1 cross-version, cross-loader foundation for Minecraft mod projects. It is inspired by Architectury API’s shared-code workflow, but it intentionally starts with a narrower contract: entrypoints, lifecycle hooks, simple block/item registration, creative-tab entries, literal commands, config defaults, protocol channel declarations, platform detection, data/resource generation, logging, and documented extension points.

The V1 rule is simple: expose only behavior that is safe across the supported matrix, and keep real Minecraft or loader differences in version-specific or loader-specific modules.

## Supported Targets

| Target | Loader artifact | Minecraft | Java toolchain | Status |
| --- | --- | --- | --- | --- |
| Fabric 1.20.1 | `portable-api-platform-fabric-1201` | `1.20.1` | 17 | Compile-validated |
| Forge 1.20.1 | `portable-api-platform-forge-1201` | `1.20.1` | 17 | Compile-validated |
| Fabric 1.21.1 | `portable-api-platform-fabric-1211` | `1.21.1` | 21 | Compile-validated |
| NeoForge 1.21.1 | `portable-api-platform-neoforge-1211` | `1.21.1` | 21 | Compile-validated |

Gradle itself should run on Java 21 because the pinned Fabric Loom line requires it. The build uses Gradle toolchains so 1.20.1 modules still compile with `--release 17`.

## Repository Layout

| Module | Responsibility |
| --- | --- |
| `api-core` | Pure Java shared contracts. No Minecraft or loader imports are allowed. |
| `api-mc-1201`, `api-mc-1211` | Version-specific Minecraft adapters where official API differences matter. |
| `platform-fabric-1201`, `platform-forge-1201`, `platform-fabric-1211`, `platform-neoforge-1211` | Loader bootstraps and runtime artifacts. |
| `template-common` | The one minimal copyable shared template mod implementation. |
| `template-*` | Thin loader entrypoints and metadata for the template mod. |

## Build Commands

```powershell
$env:JAVA_HOME='C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat :api-core:check validateGeneratedResources
.\gradlew.bat :api-mc-1201:compileJava :api-mc-1211:compileJava
.\gradlew.bat :platform-fabric-1201:compileJava :platform-forge-1201:compileJava :platform-fabric-1211:compileJava :platform-neoforge-1211:compileJava
.\gradlew.bat :template-fabric-1201:compileJava :template-forge-1201:compileJava :template-fabric-1211:compileJava :template-neoforge-1211:compileJava
```

Use `.\gradlew.bat releaseBuild` for the release gate. Development run tasks are intentionally separate, for example `:platform-fabric-1201:runClient`, `:platform-forge-1201:runServer`, and `:template-neoforge-1211:runGameTestServer`.

## Minimal Shared Mod

```java
public final class MyMod implements PortableMod {
    @Override
    public void initialize(PortableModContext context) {
        var block = context.content().registerSimpleBlock(
                "example_block",
                PortableBlockSettings.stoneLike(),
                PortableItemSettings.defaults()
        );
        context.content().addToCreativeTab(PortableCreativeTabKey.BUILDING_BLOCKS, block.item());
        context.commands().registerLiteral("my_mod", 2, command -> {
            command.reply("Hello from " + context.platform().loader());
            return 1;
        });
    }
}
```

Fabric entrypoints call `Fabric1201Bootstrap.initialize(modId, new MyMod())` or `Fabric1211Bootstrap.initialize(...)`. Forge and NeoForge constructors call the matching `Forge1201Bootstrap` or `NeoForge1211Bootstrap` with the mod event bus.

## V1 Limitations

Portable API V1 does not attempt feature parity with mature APIs. Networking is declaration-only, data generation writes stable resource paths rather than wrapping every loader data-provider type, and complex blocks/items belong in version-specific or loader-specific modules. These are compatibility boundaries, not TODO placeholders.

See `docs/` for architecture, setup, migration, troubleshooting, and publishing details.
