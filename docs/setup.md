# Setup

## Requirements

- Java 21 for running Gradle.
- Java 17 and Java 21 toolchains available locally or resolvable by Gradle.
- IntelliJ IDEA 2025.3 or newer recommended.
- Network access to Maven Central, Fabric Maven, MinecraftForge Maven, and NeoForged Maven.

## Import

Open the repository root in IntelliJ and import as a Gradle project. The committed run configurations are Gradle run configurations and use `$PROJECT_DIR$`, so they do not contain local paths.

## Development Commands

```powershell
.\gradlew.bat :api-core:check validateGeneratedResources
.\gradlew.bat :platform-fabric-1201:runClient
.\gradlew.bat :platform-forge-1201:runServer
.\gradlew.bat :template-neoforge-1211:runGameTestServer
```

Use target-specific run tasks while developing. Use `releaseBuild` before publishing.
