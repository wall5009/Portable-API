# API Usage

The shared mod implements `PortableMod`:

```java
public final class ExampleMod implements PortableMod {
    @Override
    public void initialize(PortableModContext context) {
        context.content().registerItem("example_item", PortableItemSettings.defaults());
    }
}
```

Register a command tree:

```java
PortableCommandNode.Builder root = PortableCommandTree.literal("example");
root.thenLiteral("status").executes(ctx -> {
    ctx.reply("Running");
    return 1;
});
context.commands().registerTree(new PortableCommandTree(root.build()));
```

Use typed config through `PortableConfigManager.registerTyped`. The old raw `PortableConfigSpec` path still works and is deprecated for compatibility.

Use `context.diagnostics().logSummary()` when diagnosing environment setup.
