# Module Layout

Use `api-core` for identifiers, lifecycle contracts, command declarations, typed config, packet codecs, content declarations, and structured resource builders.

Use `api-mc-*` for code that imports `net.minecraft.*` but not loader APIs.

Use `platform-*` for loader APIs, mod metadata, loader event buses, registry integration, and publication runtime jars.

Use `template-common` and the four `template-*` modules only for scaffold examples and release validation.
