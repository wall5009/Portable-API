# Extension Guidelines

Portable API expands by adding narrow, typed service surfaces.

## Adding a Capability

1. Add a `Capability` enum constant.
2. Add a public service interface or extend an existing one with default methods only.
3. Add runtime-core behavior or an explicit unsupported path.
4. Add target capability status in both common runtime factories.
5. Update `portable-api-target.properties` for every platform.
6. Add tests and documentation.
7. Add an ADR if the capability changes architecture or exposes Minecraft concepts.

## Adding a Loader Target

1. Add a platform module under `platforms/`.
2. Reuse an existing versioned common module or add a new one.
3. Add a blank validation consumer.
4. Add metadata and publication identity.
5. Add the target to CI and `validateAllTargets`.

## API Design Requirements

- Prefer records for immutable values.
- Prefer interfaces for services.
- Avoid exposing mutable collections.
- Avoid loader or Minecraft classes in public signatures.
- Report unsupported behavior through `CapabilityReport`.
