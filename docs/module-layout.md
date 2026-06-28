# Module Layout

Use this layout for real mods copied from the template:

```text
common/
  src/main/java/.../MyMod.java
fabric-1201/
  src/main/java/.../MyFabric1201Entrypoint.java
forge-1201/
  src/main/java/.../MyForge1201Mod.java
fabric-1211/
  src/main/java/.../MyFabric1211Entrypoint.java
neoforge-1211/
  src/main/java/.../MyNeoForge1211Mod.java
```

Keep common code free of loader imports. If code must touch Minecraft classes, put it in a version-specific common module. If code must touch loader classes, put it in the loader module.

The repository’s dependency-boundary check enforces the same rule for Portable API itself.
