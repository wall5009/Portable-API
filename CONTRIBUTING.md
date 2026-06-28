# Contributing

Portable API changes should keep shared-code promises honest. Do not move Minecraft or loader-only types into `api-core`, and do not add a common abstraction when the supported targets have materially different behavior.

## Local Checks

Run Gradle with Java 21:

```powershell
$env:JAVA_HOME='C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat check validateGeneratedResources
```

For target work, compile the affected modules and at least one template module for the same Minecraft version. Before release, run:

```powershell
.\gradlew.bat releaseBuild
```

## API Rules

- `api-core` is pure Java and must not import `net.minecraft`, Fabric, Forge, or NeoForge packages.
- `api-mc-*` may import Minecraft classes but must not import loader packages.
- `platform-*` modules may import only their target loader family.
- V1 abstractions must fail with actionable errors when an operation is unsupported.
- Comments should explain lifecycle, compatibility, and non-obvious implementation decisions. Do not add comments that restate a single obvious statement.

## Version Pins

Dependency pins live in `gradle.properties`. Update them only after checking official Maven metadata and compiling every affected target. Document the reason in the pull request.

## Pull Requests

Open PRs with:

- The target matrix affected.
- Commands run locally.
- Any known in-game validation not performed and why.
- Screenshots or logs only when they clarify a client/server behavior change.
