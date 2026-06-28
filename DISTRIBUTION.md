# Portable API distribution

Run the complete release build from the repository root:

```bat
gradlew.bat clean fullDistributionBuild -PreleaseChannel=release
```

Use `alpha`, `beta`, or `release` for `releaseChannel`. The legacy command
`releaseBuild` is an alias for the same complete task.

The output is created in:

```text
build/release/portable-api-<version>/
build/distributions/portable-api-full-distribution-<version>.zip
```

The release folder is divided by destination:

- `github/`: four player-facing API JARs and release documents.
- `curseforge/`: one directory per Minecraft/loader target with its JAR and upload metadata.
- `modrinth/`: one directory per target with its primary JAR, metadata, sources, and Javadoc.
- `maven/`: a repository-layout Maven tree and a bundle ZIP.
- `developer-artifacts/`: template runtime JARs and API companion artifacts; do not publish template JARs as player API downloads.

## Maven Central signing

Maven Central requires PGP signatures. Provide an ASCII-armored private key and
its password, then request Central validation:

```bat
set SIGNING_KEY=<ASCII-armored-private-key>
set SIGNING_PASSWORD=<key-password>
gradlew.bat clean fullDistributionBuild -PreleaseChannel=release -PmavenCentral=true
```

On PowerShell, use `$env:SIGNING_KEY` and `$env:SIGNING_PASSWORD` instead of
`set`.

## Direct remote Maven publishing

The build also exposes `publishApiToRemoteRepository`, which publishes only the
seven API Maven modules, never the template modules:

```bat
set MAVEN_PUBLISH_URL=https://your.maven.repository/releases
set MAVEN_USERNAME=your-username
set MAVEN_PASSWORD=your-password
gradlew.bat publishApiToRemoteRepository
```

Review `build/release/portable-api-<version>/MANUAL-DISTRIBUTION.md` after every
build. It contains the exact generated filenames and target metadata for that
version.
