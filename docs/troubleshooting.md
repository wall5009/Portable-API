# Troubleshooting

## Gradle says Loom requires Java 21

Run Gradle with Java 21. The build still compiles Minecraft 1.20.1 modules with a Java 17 toolchain.

## Creative tab constants are inaccessible

That is expected in official mappings for supported versions. Portable API constructs public `ResourceKey` values in version-specific adapters.

## Fabric template run cannot find `portable_api`

During local development, run the API Fabric target and template target together or install the built `platform-fabric-*` API jar next to the template jar. The template metadata models the production dependency explicitly.

## A common module needs Minecraft classes

Create or use a version-specific common module. Do not add Minecraft imports to `api-core` or to a mod’s pure common module.

## Release publishing skipped

The release workflow honors dry-run mode by default and requires repository secrets for Maven, Modrinth, and CurseForge publishing.
