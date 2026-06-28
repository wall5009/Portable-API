# Template Mod

The repository contains exactly one template mod:

- `template-common`
- `template-fabric-1201`
- `template-forge-1201`
- `template-fabric-1211`
- `template-neoforge-1211`

It demonstrates:

- Shared `PortableMod` implementation.
- Simple block and item registration.
- Creative-tab entries.
- A literal command.
- A default config file.
- A declared network channel.
- Generated resource output.
- Thin loader entrypoints.

To copy it into a new repository, keep the common module and only the loader targets you intend to ship. Rename package names, mod ids, Gradle artifact names, and metadata together.
