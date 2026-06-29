# API Stability

Portable API uses four marker annotations:

- `@PublicApi`
- `@ExperimentalApi`
- `@InternalApi`
- `@Since`

Public v1.0.0 core classes and methods are checked by `checkApiCompatibility` against `config/api/v1.0.0-api.txt`.

Compatibility policy:

- Prefer additive changes.
- Use default interface methods for new SPI hooks where possible.
- Deprecate instead of deleting public methods.
- Keep `api-core` independent from Minecraft and loader packages.
