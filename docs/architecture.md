# Architecture

Portable API keeps boundaries explicit:

- `api-core`: pure Java API and contracts. No Minecraft, Fabric, Forge, or NeoForge imports.
- `api-mc-1201` and `api-mc-1211`: Minecraft-version adapters and Brigadier conversion helpers.
- `platform-*`: loader bootstraps, mod metadata, registry bridges, lifecycle event bridges, platform services, and loader-specific boundaries.
- `template-common`: one shared template mod.
- `template-*`: thin loader entrypoints and metadata for the template.

Platform jars include `api-core` and the matching `api-mc-*` output so consumers install one Portable API jar matching their loader and Minecraft version.

Complex systems whose semantics differ materially are intentionally not abstracted: block entities, menus, renderers, entities, capabilities, attachments, and tracking-specific networking.
