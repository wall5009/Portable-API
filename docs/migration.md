# Migration Procedures

## From Loader-Specific Code

1. Move common initialization into a class that accepts `ModContext`.
2. Replace direct logging calls with `context.logger()`.
3. Replace direct loader checks with `context.platform()` and `context.capabilities()`.
4. Replace command registration with `context.commands().register`.
5. Replace config defaults with `context.config().register`.
6. Keep rendering, screen registration, worldgen, and persistent attachments behind existing loader modules until Portable API adds typed support.

## Between Minecraft Versions

Keep consumer code in the public API surface. If code imports `net.minecraft.*`, isolate it behind a versioned module owned by the consuming project.

## Deprecation Handling

Deprecated API remains available until the next major version unless a security or loader-breaking issue makes that impossible. Deprecations must include replacement guidance in Javadocs and documentation.
