# Migration

For the specific v1.0.0 to v1.1.0 guide, see `migration-1.0.0-to-1.1.0.md`.

General rules:

- Keep using v1.0.0 APIs unless you need a v1.1 feature.
- Replace raw config files with typed config when validation or reload snapshots matter.
- Prefer `registerTree` for new commands; `registerLiteral` remains functional.
- Keep loader-specific networking behavior in platform modules when it cannot be represented honestly by the common contract.
