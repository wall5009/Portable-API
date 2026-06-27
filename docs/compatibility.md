# Compatibility Guarantees

Portable API follows semantic versioning.

## 1.x Guarantees

- Public package names remain stable.
- Public class, interface, method, field, and enum names are not removed.
- Method signatures are not changed incompatibly.
- New default methods may be added to interfaces.
- New enum constants may be added.
- Internal packages may change without notice.

## Deprecation Policy

Deprecations must:

- Include replacement guidance.
- Stay available until the next major release.
- Be documented in the changelog.

## Unsupported Operations

Unsupported or deferred features must fail predictably. They should use `UnsupportedFeatureException` when the failure maps to a `Capability`; otherwise they should use a specific `UnsupportedOperationException` message.
