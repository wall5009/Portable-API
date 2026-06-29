# Distribution

`fullDistributionBuild` creates the release layout under `build/release/portable-api-<version>` and the full archive under `build/distributions`.

## Commands

```powershell
.\gradlew.bat clean check fullDistributionBuild
.\gradlew.bat releaseBuild
```

## Output Layout

- `github/`: release notes, checksums, and the four API runtime jars.
- `curseforge/<target>/`: one API runtime jar plus upload metadata per target.
- `modrinth/<target>/`: one API runtime jar plus upload metadata per target.
- `maven/repository/`: staged Maven repository for the seven API publications.
- `developer-artifacts/template-runtime-jars/`: template jars for scaffold validation only.

## Player-Facing Files

Upload only:

- `portable-api-platform-fabric-1201-1.1.0.jar`
- `portable-api-platform-forge-1201-1.1.0.jar`
- `portable-api-platform-fabric-1211-1.1.0.jar`
- `portable-api-platform-neoforge-1211-1.1.0.jar`

Fabric files require Fabric API. Forge and NeoForge files have no additional Portable API runtime dependency.

## Remote Publishing

Remote Maven publishing requires:

- `MAVEN_PUBLISH_URL`
- `MAVEN_USERNAME`
- `MAVEN_PASSWORD`

Release workflows require the relevant CurseForge and Modrinth project IDs and tokens. Dry runs perform validation without remote writes.
