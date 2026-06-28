# V1 Limitations

Portable API V1 intentionally does not cover:

- Custom block entities.
- Client renderers.
- GUI/config screens.
- Datapack registry bootstrapping.
- Full typed data providers.
- Cross-loader packet send/receive handlers.
- Mixin or access-widener/access-transformer coordination.

These areas are extension points, not abandoned work. They need design that respects version and loader differences.
