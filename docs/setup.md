# Setup

## Requirements

- JDK 21 for the Gradle daemon and the full build matrix.
- Gradle wrapper from this repository.
- Network access for Maven dependencies.
- IntelliJ IDEA or VS Code for Loom run configuration support.

## Local Verification

```powershell
./gradlew.bat validateAllTargets
```

This runs Java tests, formatting checks, docs link checks, common purity checks, platform builds, and validation mod builds.

## Targeted Builds

```powershell
./gradlew.bat :platforms:fabric-1.20.1:build
./gradlew.bat :platforms:forge-1.20.1:build
./gradlew.bat :platforms:fabric-1.21.1:build
./gradlew.bat :platforms:neoforge-1.21.1:build
```

## IDE Import

Import the root Gradle project. The repository declares `toolchainVersion=21` in `gradle/gradle-daemon-jvm.properties`, so Gradle wrapper launches can select an installed Java 21 daemon even if the shell `JAVA_HOME` points at Java 17. In IntelliJ, set **Gradle JVM** to a JDK 21 installation or to a compatible auto-detected Gradle JVM.

The 1.20.1 modules still compile with Java 17 bytecode through `options.release = 17`; Java 21 is required to run Gradle and Loom, not because Forge or Fabric 1.20.1 artifacts are emitted as Java 21.
